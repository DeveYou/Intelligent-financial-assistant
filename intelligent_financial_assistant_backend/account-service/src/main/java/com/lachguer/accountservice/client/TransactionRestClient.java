package com.lachguer.accountservice.client;

import com.lachguer.accountservice.dto.TransactionRequestDTO;
import com.lachguer.accountservice.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionRestClient {

    @PostMapping("/api/transactions")
    TransactionResponseDTO createTransaction(@RequestBody TransactionRequestDTO request,
                                             @RequestHeader(value = "Authorization", required = false) String authorizationHeader);

    @GetMapping("/api/transactions/account/{accountId}")
    List<TransactionResponseDTO> getTransactionsByAccount(@PathVariable("accountId") Long accountId,
                                                          @RequestHeader(value = "Authorization", required = false) String authorizationHeader);
}
