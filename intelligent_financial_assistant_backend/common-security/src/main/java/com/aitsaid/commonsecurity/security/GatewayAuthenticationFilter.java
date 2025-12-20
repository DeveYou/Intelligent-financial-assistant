package com.aitsaid.commonsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);
    private static final String HEADER_USER = "X-Auth-User";
    private static final String HEADER_ROLES = "X-Auth-Roles";
    private static final String HEADER_TOKEN_VALIDATED = "X-Auth-Token-Validated";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        log.info("Processing request: {} {}", method, requestUri);

        // DEBUG: Log des headers
        if (log.isDebugEnabled()) {
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.debug("Header: {} = {}", headerName, request.getHeader(headerName));
            }
        }

        // Essayer d'authentifier de deux manières différentes :
        AuthenticationResult authResult = authenticateViaGatewayHeaders(request);

        if (!authResult.isAuthenticated()) {
            authResult = authenticateViaBearerToken(request);
        }

        if (authResult.isAuthenticated()) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            authResult.getUsername(),
                            authResult.getRawToken(),
                            authResult.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("User '{}' authenticated with roles: {}",
                    authResult.getUsername(),
                    authResult.getAuthorities());
        } else {
            log.debug("No valid authentication found for request: {} {}", method, requestUri);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Mode 1: Authentification via headers Gateway (appel via API Gateway)
     */
    private AuthenticationResult authenticateViaGatewayHeaders(HttpServletRequest request) {
        String username = request.getHeader(HEADER_USER);
        String rolesHeader = request.getHeader(HEADER_ROLES);
        String tokenValidated = request.getHeader(HEADER_TOKEN_VALIDATED);

        if (username != null && rolesHeader != null && "true".equals(tokenValidated)) {
            List<SimpleGrantedAuthority> authorities = parseRoles(rolesHeader);
            String rawToken = extractRawToken(request);

            log.info("Authentication via Gateway headers - User: {}, Roles: {}", username, rolesHeader);
            return new AuthenticationResult(true, username, rawToken, authorities);
        }

        return AuthenticationResult.notAuthenticated();
    }

    /**
     * Mode 2: Authentification via Token Bearer (appels inter-services)
     */
    private AuthenticationResult authenticateViaBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Valider et parser le token JWT
                Claims claims = parseJwtToken(token);
                String username = claims.getSubject();

                // Extraire les rôles du token
                List<String> roles = extractRolesFromToken(claims);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Extraire userId si présent
                Object userId = claims.get("userId");
                if (userId != null) {
                    log.info("User ID from token: {}", userId);
                }

                log.info("Authentication via Bearer Token - User: {}, Roles: {}", username, roles);
                return new AuthenticationResult(true, username, token, authorities);

            } catch (Exception e) {
                log.warn("Invalid Bearer token: {}", e.getMessage());
            }
        }

        return AuthenticationResult.notAuthenticated();
    }

    private Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRolesFromToken(Claims claims) {
        try {
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List) {
                return (List<String>) rolesObj;
            }
        } catch (Exception e) {
            log.warn("Could not extract roles from token: {}", e.getMessage());
        }

        // Fallback: essayer le champ "role" (singulier)
        Object roleObj = claims.get("role");
        if (roleObj != null) {
            return List.of(roleObj.toString());
        }

        return List.of("ROLE_USER"); // Rôle par défaut
    }

    private List<SimpleGrantedAuthority> parseRoles(String rolesHeader) {
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String extractRawToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        String trimmed = jwtSecret == null ? "" : jwtSecret.trim();
        try {
            if (looksLikeBase64(trimmed)) {
                keyBytes = Decoders.BASE64.decode(trimmed);
            } else {
                keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
            }
        } catch (DecodingException | IllegalArgumentException e) {
            keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean looksLikeBase64(String value) {
        if (value == null) return false;
        String v = value.trim();
        return v.length() % 4 == 0 && v.matches("[A-Za-z0-9+/=]+");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("Checking filter for: {} {}", method, path);

        // Ne PAS filtrer :
        // 1. Actuator endpoints
        if (path.startsWith("/actuator/")) {
            return true;
        }

        // 2. POST /api/accounts/register (inscription - PUBLIC)
        if (path.equals("/api/accounts/register") && "POST".equals(method)) {
            log.info("SKIP FILTER: Public registration endpoint");
            return true;
        }

        log.info("SHOULD FILTER: Protected endpoint");
        return false;
    }

    /**
     * Classe helper pour stocker le résultat d'authentification
     */
    private static class AuthenticationResult {
        private final boolean authenticated;
        private final String username;
        private final String rawToken;
        private final List<SimpleGrantedAuthority> authorities;

        private AuthenticationResult(boolean authenticated, String username,
                                     String rawToken, List<SimpleGrantedAuthority> authorities) {
            this.authenticated = authenticated;
            this.username = username;
            this.rawToken = rawToken;
            this.authorities = authorities;
        }

        static AuthenticationResult notAuthenticated() {
            return new AuthenticationResult(false, null, null, null);
        }

        boolean isAuthenticated() { return authenticated; }
        String getUsername() { return username; }
        String getRawToken() { return rawToken; }
        List<SimpleGrantedAuthority> getAuthorities() { return authorities; }
    }
}
