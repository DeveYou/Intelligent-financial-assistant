package com.khaoula.transactionsservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author radouane
 **/
@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    AccountResponse getAccountById(@PathVariable("id") Long id,
                                   @RequestHeader("Authorization") String authorization);

    @GetMapping("/api/accounts/iban/{iban}")
    AccountResponse getAccountByIban(@PathVariable("iban") String iban,
                                     @RequestHeader("Authorization") String authorization);

    @PostMapping("/api/accounts/{id}/balance")
    void updateBalance(@PathVariable("id") Long id,
                       @RequestBody BalanceUpdateRequest request,
                       @RequestHeader("Authorization") String authorization);

    @Data
    class AccountResponse {
        private Long id;
        private String iban;
        private Double balance;
        private Long userId;
        private Boolean isActive;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class BalanceUpdateRequest {
        private Double amount;
        private String operation; // "ADD" or "SUBTRACT"
    }
}
