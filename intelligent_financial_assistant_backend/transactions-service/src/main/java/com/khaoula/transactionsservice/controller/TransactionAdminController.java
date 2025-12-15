package com.khaoula.transactionsservice.controller;


import com.khaoula.transactionsservice.dto.TransactionFilterDTO;
import com.khaoula.transactionsservice.dto.TransactionRequestDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionAdminController {

    private final TransactionService transactionService;

    /**
     * Récupérer toutes les transactions avec filtres et pagination
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bankAccountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            Authentication authentication) {

        log.info("Admin {} retrieving transactions", authentication.getName());

        TransactionFilterDTO filter = new TransactionFilterDTO();
        filter.setUserId(userId);
        filter.setBankAccountId(bankAccountId);
        // TODO: Parse type, status, dates
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactions(filter);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Récupérer une transaction par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Admin {} retrieving transaction {}", authentication.getName(), id);
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Récupérer une transaction par référence
     */
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> getByReference(
            @PathVariable String reference,
            Authentication authentication) {

        log.info("Admin {} retrieving transaction by reference {}", authentication.getName(), reference);
        TransactionResponseDTO transaction = transactionService.getTransactionByReference(reference);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Récupérer toutes les transactions d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(
            @PathVariable Long userId,
            Authentication authentication) {

        log.info("Admin {} retrieving transactions for user {}", authentication.getName(), userId);
        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Récupérer toutes les transactions d'un compte
     */
    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getAccountTransactions(
            @PathVariable Long accountId,
            Authentication authentication) {

        log.info("Admin {} retrieving transactions for account {}", authentication.getName(), accountId);
        List<TransactionResponseDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Créer un dépôt (admin peut faire des opérations pour n'importe quel utilisateur)
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createDeposit(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestParam Long userId,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating deposit for user {}", authentication.getName(), userId);
        TransactionResponseDTO response = transactionService.createDeposit(request, userId, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Créer un retrait
     */
    @PostMapping("/withdrawal")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createWithdrawal(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestParam Long userId,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating withdrawal for user {}", authentication.getName(), userId);
        TransactionResponseDTO response = transactionService.createWithdrawal(request, userId, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Créer un transfert
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createTransfer(
            @Valid @RequestBody TransferRequestDTO request,
            @RequestParam Long userId,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating transfer for user {}", authentication.getName(), userId);
        TransactionResponseDTO response = transactionService.createTransfer(request, userId, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer les statistiques des transactions
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionService.TransactionStatsDTO> getStats(
            Authentication authentication) {

        log.info("Admin {} retrieving transaction statistics", authentication.getName());
        TransactionService.TransactionStatsDTO stats = transactionService.getTransactionStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Annuler une transaction (admin peut annuler n'importe quelle transaction PENDING)
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> cancelTransaction(
            @PathVariable Long id,
            @RequestParam Long userId,
            Authentication authentication) {

        log.info("Admin {} cancelling transaction {} for user {}",
                authentication.getName(), id, userId);

        TransactionResponseDTO cancelled = transactionService.cancelTransaction(id, userId);
        return ResponseEntity.ok(cancelled);
    }


}
