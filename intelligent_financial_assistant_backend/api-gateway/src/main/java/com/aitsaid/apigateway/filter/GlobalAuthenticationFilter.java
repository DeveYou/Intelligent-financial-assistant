package com.aitsaid.apigateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("DEBUG GLOBAL FILTER: Request to " + exchange.getRequest().getURI().getPath());
        if (validator.isSecured.test(exchange.getRequest())) {
            System.out.println("DEBUG GLOBAL FILTER: Route is secured");
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {
                if (!jwtUtil.isTokenValid(authHeader)) {
                    System.out.println("DEBUG GLOBAL FILTER: Token invalid");
                    return onError(exchange, "Invalid Access Token", HttpStatus.UNAUTHORIZED);
                }

                Claims claims = jwtUtil.extractAllClaims(authHeader);
                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                
                System.out.println("DEBUG GLOBAL FILTER: Token valid. User: " + username + ", Roles: " + roles);

                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .header("X-Auth-User", username)
                        .header("X-Auth-Roles", String.join(",", roles))
                        .header("X-Auth-Token-Validated", "true")
                        .build();

                return chain.filter(exchange.mutate().request(request).build());

            } catch (Exception e) {
                System.out.println("DEBUG GLOBAL FILTER: Exception " + e.getMessage());
                return onError(exchange, "Unauthorized access to application", HttpStatus.UNAUTHORIZED);
            }
        }
        System.out.println("DEBUG GLOBAL FILTER: Route NOT secured");
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
