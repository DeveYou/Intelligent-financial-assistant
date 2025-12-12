package com.lachguer.accountservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration to enable a global Bearer JWT security scheme.
 * This adds the "Authorize" button in Swagger UI so you can paste
 * a token once ("Bearer <token>") and it will be applied to all secured endpoints.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_KEY = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("Account Service API")
                        .description("Endpoints for accounts and transactions")
                        .version("v1"))
                .schemaRequirement(BEARER_KEY, bearerScheme)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_KEY));
    }
}
