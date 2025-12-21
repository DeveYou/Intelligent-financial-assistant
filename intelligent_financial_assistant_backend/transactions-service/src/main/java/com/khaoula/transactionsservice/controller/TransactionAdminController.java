package com.khaoula.transactionsservice.controller;

import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.TransactionFilterDTO;
import com.khaoula.transactionsservice.dto.TransactionRequestDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/admin/transactions")
public class TransactionAdminController {

    private static final Logger log = LoggerFactory.getLogger(TransactionAdminController.class);
    private final TransactionService transactionService;

    public TransactionAdminController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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

        // Parser le type
        if (type != null && !type.isEmpty()) {
            try {
                filter.setType(TransactionType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid transaction type: {}", type);
                return ResponseEntity.badRequest().build();
            }
        }

        // Parser le status
        if (status != null && !status.isEmpty()) {
            try {
                filter.setStatus(TransactionStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid transaction status: {}", status);
                return ResponseEntity.badRequest().build();
            }
        }

        // Parser les dates
        if (startDate != null && !startDate.isEmpty()) {
            try {
                filter.setStartDate(OffsetDateTime.parse(startDate));
            } catch (DateTimeParseException e) {
                log.warn("Invalid start date format: {}", startDate);
                return ResponseEntity.badRequest().build();
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                filter.setEndDate(OffsetDateTime.parse(endDate));
            } catch (DateTimeParseException e) {
                log.warn("Invalid end date format: {}", endDate);
                return ResponseEntity.badRequest().build();
            }
        }

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
            @PathVariable String reference) {

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
     * Créer un dépôt (admin peut faire des opérations pour n'importe quel
     * utilisateur)
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createDeposit(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating deposit for account {}", authentication.getName(), request.getBankAccountId());
        TransactionResponseDTO response = transactionService.createDeposit(request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Créer un retrait
     */
    @PostMapping("/withdrawal")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createWithdrawal(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating withdrawal for account {}", authentication.getName(), request.getBankAccountId());
        TransactionResponseDTO response = transactionService.createWithdrawal(request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Créer un transfert
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> createTransfer(
            @Valid @RequestBody TransferRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        log.info("Admin {} creating transfer for account {}", authentication.getName(), request.getBankAccountId());
        TransactionResponseDTO response = transactionService.createTransfer(request, authHeader);
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

    @GetMapping("/stats/daily")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<com.khaoula.transactionsservice.dto.DailyTransactionStats>> getDailyStats(
            Authentication authentication) {

        log.info("Admin {} retrieving daily transaction statistics", authentication.getName());
        return ResponseEntity.ok(transactionService.getDailyStats());
    }

    /**
     * Annuler une transaction (admin peut annuler n'importe quelle transaction
     * PENDING)
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
