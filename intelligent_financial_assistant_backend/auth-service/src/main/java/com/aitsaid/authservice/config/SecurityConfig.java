package com.aitsaid.authservice.config;

import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de sécurité pour Auth-Service
 * La validation JWT principale se fait au Gateway, mais on garde une couche locale
 * pour la défense en profondeur
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ====== Endpoints publics (sans authentification) ======
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // ====== Endpoints Auth protégés (authentification requise) ======
                        .requestMatchers("/auth/logout", "/auth/validate-token").authenticated()

                        // ====== Endpoints ADMIN (rôle ROLE_ADMIN requis) ======
                        .requestMatchers("/admin/**").hasAuthority(String.valueOf(Role.ROLE_ADMIN))

                        // ====== Endpoints USER (rôle ROLE_USER requis) ======
                        .requestMatchers("/user/**").hasAuthority(String.valueOf(Role.ROLE_USER))

                        // ====== Tous les autres endpoints nécessitent une authentification ======
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}