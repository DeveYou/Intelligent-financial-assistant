package com.lachguer.accountservice.client;

import com.lachguer.accountservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Minimal Feign client for AUTH-SERVICE to validate/ping tokens when needed.
 * This is optional and can be extended based on the real auth API.
 */
@FeignClient(name = "AUTH-SERVICE")
public interface AuthRestClient {

    /**
     * Example endpoint to validate a token. Adjust path according to AUTH-SERVICE API.
     */
    @GetMapping("/api/auth/validate")
    Boolean validateToken(@RequestHeader("Authorization") String authorizationHeader);

    @GetMapping("/admin/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
