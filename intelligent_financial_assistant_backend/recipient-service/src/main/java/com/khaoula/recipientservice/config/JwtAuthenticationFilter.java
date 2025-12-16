package com.khaoula.recipientservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret:default-secret-key-change-in-production}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String userId = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            try {
                // Parser et valider le JWT
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                // OPTION 1: Récupérer le userId depuis un claim spécifique comme Integer/Long
                Integer userIdInt = claims.get("userId", Integer.class);
                if (userIdInt != null) {
                    userId = userIdInt.toString(); // Convertir en String
                }

                // OPTION 2: Si userId n'est pas dans les claims, utiliser le subject
                if (userId == null) {
                    userId = claims.getSubject(); // Subject peut être l'email
                }

                // Log pour débogage
                logger.info("JWT decoded - Subject: " + claims.getSubject() +
                        ", UserId from claims: " + userIdInt +
                        ", Final userId: " + userId);

            } catch (Exception e) {
                logger.error("JWT validation failed: " + e.getMessage(), e);
            }
        } else {
            logger.warn("No Authorization header or invalid format");
        }

        // Si le token est valide et qu'il n'y a pas d'authentification actuelle
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Créer l'authentification
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,  // Principal = userId (String)
                            null,    // Credentials
                            Collections.emptyList() // Autorités
                    );

            // Ajouter des détails de la requête
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Authentication set for user: " + userId);
        } else if (userId == null) {
            logger.warn("No valid user ID extracted from JWT");
        }

        filterChain.doFilter(request, response);
    }
}