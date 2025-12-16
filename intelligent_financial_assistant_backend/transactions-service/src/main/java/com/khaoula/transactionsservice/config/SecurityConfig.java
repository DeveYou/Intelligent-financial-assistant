package com.khaoula.transactionsservice.config;

import com.aitsaid.commonsecurity.security.MicroserviceSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MicroserviceSecurityConfig.class)
public class SecurityConfig {
}
