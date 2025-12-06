package com.aitsaid.apigateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * Détermine quelles routes sont publiques (sans authentification)
 * Adapté pour le routage dynamique via Eureka
 */
@Component
public class RouteValidator {

    /**
     * Liste des endpoints publics (accessibles sans token JWT)
     * Format avec routage dynamique : /[service-name]/[endpoint]
     */
    public static final List<String> openApiEndpoints = List.of(
            // Auth Service - Endpoints publics
            "/auth-service/auth/register",
            "/auth-service/auth/login",

            // Eureka (si exposé)
            "/eureka"
    );

    /**
     * Prédicat qui retourne true si la route nécessite une authentification
     * (toutes les routes SAUF celles dans openApiEndpoints)
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}