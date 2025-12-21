package com.khaoula.transactionsservice.controller;

import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/transactions")
public class TransactionUserController {

    private static final Logger log = LoggerFactory.getLogger(TransactionUserController.class);
    private final TransactionService transactionService;

    public TransactionUserController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Effectuer un transfert
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransferRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        TransactionResponseDTO response = transactionService.createTransfer(request, authHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer ses propres transactions
     */
    @GetMapping("/my-transactions")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        log.info("User {} retrieving their transactions", userId);

        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Helper method pour récupérer le userId
     * Ordre de priorité:
     * 1. Header X-Auth-User-Id (propagé par le Gateway)
     * 2. Authentication (username qui est l'email)
     */
    private Long getUserIdFromHeader(String userIdHeader, Authentication authentication) {
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("Invalid X-Auth-User-Id header: {}", userIdHeader);
            }
        }

        log.warn("X-Auth-User-Id header not found. Authentication principal: {}",
                authentication != null ? authentication.getName() : "null");

        throw new RuntimeException("Unable to determine user ID. X-Auth-User-Id header is missing.");
    }
}
