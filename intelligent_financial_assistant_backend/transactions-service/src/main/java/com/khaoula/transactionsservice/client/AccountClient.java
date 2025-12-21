package com.khaoula.transactionsservice.client;

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

    class AccountResponse {
        private Long id;
        private String iban;
        private Double balance;
        private Long userId;
        private Boolean isActive;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getIban() {
            return iban;
        }

        public void setIban(String iban) {
            this.iban = iban;
        }

        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }

    class BalanceUpdateRequest {
        private Double amount;
        private String operation; // "ADD" or "SUBTRACT"

        public BalanceUpdateRequest() {
        }

        public BalanceUpdateRequest(Double amount, String operation) {
            this.amount = amount;
            this.operation = operation;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }
}
