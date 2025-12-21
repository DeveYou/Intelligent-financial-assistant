package com.lachguer.accountservice.web;

import com.lachguer.accountservice.dto.*;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<BankAccountResponseDTO> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public BankAccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/iban/{iban}")
    public BankAccountResponseDTO getAccountByIban(@PathVariable String iban) {
        return accountService.getAccountByIBAN(iban);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<BankAccountResponseDTO> getAccountsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        System.out.println("Headers received in getAccountsByUserId:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        return accountService.getAccountsByUserId(userId);
    }

    @PostMapping("/register")
    public BankAccountResponseDTO createAccountOnRegister(
            @RequestBody @Valid BankAccountRequestDTO bankAccountRequestDTO) {
        log.info("Creating account during registration for user: {}",
                bankAccountRequestDTO.getUserId());
        return accountService.addAccount(bankAccountRequestDTO);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public BankAccountResponseDTO addAccount(@RequestBody @Valid BankAccountRequestDTO bankAccountRequestDTO) {
        return accountService.addAccount(bankAccountRequestDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public BankAccount updateAccount(@PathVariable Long id, @RequestBody @Valid BankAccountUpdateDTO updateDTO) {
        return accountService.updateAccount(id, updateDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionResponseDTO> getAccountTransactions(@PathVariable("id") Long accountId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return accountService.getTransactionsForAccount(accountId, authorizationHeader);
    }

    @PostMapping("/{id}/transactions")
    public TransactionResponseDTO createAccountTransaction(@PathVariable("id") Long accountId,
            @RequestBody @Valid TransactionRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return accountService.createTransactionForAccount(accountId, request, authorizationHeader);
    }

    /**
     * Récupérer le nombre total des comptes (ADMIN uniquement)
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> countUsers(Authentication authentication) {
        log.info("Admin {} retrieving user count", authentication.getName());
        return ResponseEntity.ok(accountService.countUsers());
    }

    @GetMapping("/stats/distribution")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AccountDistributionDTO>> getAccountDistribution(Authentication authentication) {
        log.info("Admin {} retrieving account distribution stats", authentication.getName());
        return ResponseEntity.ok(accountService.getAccountDistribution());
    }

    @PostMapping("/{id}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable Long id,
            @RequestBody BalanceUpdateRequest request) {
        accountService.updateBalance(id, request.getAmount(), request.getOperation());
        return ResponseEntity.ok().build();
    }

    public static class BalanceUpdateRequest {
        private Double amount;
        private String operation; // "ADD" ou "SUBTRACT"

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
