package com.aitsaid.commonsecurity.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de sécurité par défaut pour les microservices
 * Peut être surchargée dans chaque microservice si nécessaire
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity() // Active @PreAuthorize, @PostAuthorize, etc.
public class MicroserviceSecurityConfig {

    private final GatewayAuthenticationFilter gatewayAuthenticationFilter;

    public MicroserviceSecurityConfig(GatewayAuthenticationFilter gatewayAuthenticationFilter) {
        this.gatewayAuthenticationFilter = gatewayAuthenticationFilter;
    }

    /**
     * Configuration de sécurité par défaut
     * Utilise @ConditionalOnMissingBean pour permettre la surcharge dans les microservices
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultSecurityFilterChain")
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Actuator (health, metrics, etc.)
                        .requestMatchers("/actuator/**").permitAll()

                        // Tous les autres endpoints nécessitent une authentification
                        // L'autorisation fine se fait avec @PreAuthorize sur les méthodes
                        .anyRequest().authenticated())
                .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}