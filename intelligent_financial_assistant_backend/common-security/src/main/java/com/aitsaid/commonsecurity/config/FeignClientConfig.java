package com.aitsaid.commonsecurity.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            // Récupérer l'authentification actuelle
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                // Si nous avons un token (dans les credentials)
                if (authentication.getCredentials() != null) {
                    String token = authentication.getCredentials().toString();
                    template.header("Authorization", "Bearer " + token);
                    log.debug("Feign Client: Added Authorization header with Bearer token");
                }

                // Toujours ajouter l'User-Agent pour identifier les appels Feign
                template.header("User-Agent", "Java-Feign-Client");
            }
        };
    }
}