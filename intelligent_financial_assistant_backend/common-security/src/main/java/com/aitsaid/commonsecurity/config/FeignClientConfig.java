package com.aitsaid.commonsecurity.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                // Si on a une authentification (venant du GatewayAuthenticationFilter)
                if (authentication != null && authentication.isAuthenticated()) {
                    // On ne peut pas récupérer le token brut facilement car GatewayAuthenticationFilter 
                    // crée un UsernamePasswordAuthenticationToken sans credentials (null).
                    // MAIS, le Gateway a validé le token.
                    // Pour appeler un autre service sécurisé (ex: auth-service), il nous faut un token valide.
                    // Problème: Le token original est perdu si on ne le stocke pas.
                    
                    // Solution: Modifier GatewayAuthenticationFilter pour stocker le token dans les credentials
                    // ou dans les details.
                    
                    if (authentication.getCredentials() != null) {
                        String token = authentication.getCredentials().toString();
                        template.header("Authorization", "Bearer " + token);
                    }
                }
            }
        };
    }
}
