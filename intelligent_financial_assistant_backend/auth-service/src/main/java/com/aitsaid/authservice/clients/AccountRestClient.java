package com.aitsaid.authservice.clients;

import com.aitsaid.authservice.dtos.BankAccountRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountRestClient {
    @PostMapping("/api/accounts/register")
    Object createAccount(@RequestBody BankAccountRequestDTO bankAccountRequestDTO);
}
