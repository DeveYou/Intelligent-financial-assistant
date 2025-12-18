package com.notification.config;

import com.aitsaid.commonsecurity.security.GatewayAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final GatewayAuthenticationFilter gatewayAuthenticationFilter;

  public SecurityConfig(GatewayAuthenticationFilter gatewayAuthenticationFilter) {
    this.gatewayAuthenticationFilter = gatewayAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/notifications/send").permitAll() // Allow internal/system calls if needed, or secure it
            .anyRequest().authenticated())
        .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
