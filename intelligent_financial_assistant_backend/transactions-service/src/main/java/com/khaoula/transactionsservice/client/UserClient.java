package com.khaoula.transactionsservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/admin/users/{userId}")
    UserDetails getUserById(@PathVariable("userId") Long userId,
            @RequestHeader("Authorization") String authorization);

    @Data
    class UserDetails {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
