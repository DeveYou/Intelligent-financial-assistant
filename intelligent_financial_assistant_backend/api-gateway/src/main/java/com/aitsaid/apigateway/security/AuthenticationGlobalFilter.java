package com.aitsaid.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtre global qui s'applique à toutes les routes du Gateway
 * Valide JWT et propage les informations aux microservices
 */
@Component
@Slf4j
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    public AuthenticationGlobalFilter(JwtUtil jwtUtil, RouteValidator routeValidator) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Si la route est publique, on laisse passer sans validation
        if (!routeValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // Vérifier la présence du header Authorization
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing authorization header for path: {}", request.getURI().getPath());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid authorization header format for path: {}", request.getURI().getPath());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
        }

        String token = authHeader.substring(7);

        try {
            // Valider le token JWT
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token for path: {}", request.getURI().getPath());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }

            // Extraire les informations du token
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            List<String> roles = jwtUtil.extractRoles(token);

            // Propager les informations d'authentification aux microservices
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Auth-User", username)
                    .header("X-Auth-Roles", String.join(",", roles))
                    .header("X-Auth-Token-Validated", "true")
                    .build();

            log.info("Request authenticated - User: {}, Roles: {}, Path: {}",
                    username, roles, request.getURI().getPath());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("JWT validation error for path {}: {}", request.getURI().getPath(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }

    @Override
    public int getOrder() {
        return -100; // Priorité élevée pour s'exécuter en premier
    }
}