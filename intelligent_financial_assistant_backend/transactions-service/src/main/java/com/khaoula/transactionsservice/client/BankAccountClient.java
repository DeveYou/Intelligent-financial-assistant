package com.khaoula.transactionsservice.client;

import com.khaoula.transactionsservice.dto.BankAccountDTO;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", fallback = BankAccountClientFallback.class)
public interface BankAccountClient {

    @GetMapping("/api/accounts/{rib}")
    BankAccountDTO getAccountByRib(@PathVariable("rib") String rib);

    @PostMapping("/api/accounts/deposit")
    void deposit(@RequestBody DepositRequestDTO depositRequest);

    @PostMapping("/api/accounts/withdraw")
    void withdraw(@RequestBody WithdrawalRequestDTO withdrawalRequest);

    @PostMapping("/api/accounts/transfer")
    void transfer(@RequestBody TransferRequestDTO transferRequest);
}

