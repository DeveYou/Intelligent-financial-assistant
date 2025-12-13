package com.khaoula.transactionsservice.client;

import com.khaoula.transactionsservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/admin/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}

