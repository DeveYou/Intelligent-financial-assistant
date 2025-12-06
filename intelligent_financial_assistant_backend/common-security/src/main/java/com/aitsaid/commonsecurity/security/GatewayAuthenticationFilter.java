package com.aitsaid.commonsecurity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtre de sécurité partagé pour tous les microservices
 * Récupère les informations d'authentification depuis les headers propagés par le Gateway
 *
 * Headers attendus :
 * - X-Auth-User : email/username de l'utilisateur
 * - X-Auth-Roles : rôles séparés par virgules (ex: ROLE_USER,ROLE_ADMIN)
 * - X-Auth-Token-Validated : confirmation de validation du token par le Gateway
 */
@Component
@Slf4j
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER = "X-Auth-User";
    private static final String HEADER_ROLES = "X-Auth-Roles";
    private static final String HEADER_TOKEN_VALIDATED = "X-Auth-Token-Validated";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Récupérer les headers propagés par le Gateway
        String username = request.getHeader(HEADER_USER);
        String rolesHeader = request.getHeader(HEADER_ROLES);
        String tokenValidated = request.getHeader(HEADER_TOKEN_VALIDATED);

        // Si les headers sont présents et que le token a été validé par le Gateway
        if (username != null && rolesHeader != null && "true".equals(tokenValidated)) {

            // Parser les rôles
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Créer l'authentification Spring Security
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("User '{}' authenticated with roles: {}", username, authorities);
        } else {
            log.debug("No authentication headers found - request path: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}