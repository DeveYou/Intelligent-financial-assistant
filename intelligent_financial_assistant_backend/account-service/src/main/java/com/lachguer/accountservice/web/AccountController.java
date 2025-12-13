package com.lachguer.accountservice.web;

import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.dto.TransactionRequestDTO;
import com.lachguer.accountservice.dto.TransactionResponseDTO;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<BankAccountResponseDTO> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public BankAccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<BankAccountResponseDTO> getAccountsByUserId(@PathVariable Long userId, jakarta.servlet.http.HttpServletRequest request) {
        System.out.println("Headers received in getAccountsByUserId:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        return accountService.getAccountsByUserId(userId);
    }

    @PostMapping
    public BankAccountResponseDTO addAccount(@RequestBody @Valid BankAccountRequestDTO bankAccountRequestDTO) {
        return accountService.addAccount(bankAccountRequestDTO);
    }

    @PutMapping("/{id}")
    public BankAccount updateAccount(@PathVariable Long id, @RequestBody @Valid BankAccount bankAccount) {
        return accountService.updateAccount(id, bankAccount);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }

    // Transactions endpoints via TRANSACTION-SERVICE
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
}

