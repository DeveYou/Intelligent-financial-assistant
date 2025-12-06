package com.aitsaid.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Filtre Gateway pour valider JWT et propager les informations d'authentification
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Récupérer le token depuis le header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
            }

            String token = authHeader.substring(7);

            try {
                // Valider le token
                if (!jwtUtil.validateToken(token)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                }

                // Extraire les informations du token
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                List<String> roles = jwtUtil.extractRoles(token);

                // Propager les informations aux microservices via des headers personnalisés
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Auth-User", username)
                        .header("X-Auth-Roles", String.join(",", roles))
                        .build();

                log.info("Request authenticated for user: {} with roles: {}", username, roles);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("JWT validation error: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        };
    }

    public static class Config {
        // Configuration du filtre si nécessaire
    }
}