package com.khaoula.transactionsservice.controller;

import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.TransactionRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author radouane
 **/

/**
 * Controller pour les utilisateurs Flutter (ROLE_USER)
 * L'authentification est gérée par le Gateway et GatewayAuthenticationFilter
 */
@RestController
@RequestMapping("/user/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionUserController {

    private final TransactionService transactionService;

    /**
     * Effectuer un dépôt
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        // Récupérer le userId depuis le header propagé par le Gateway
        Long userId = getUserIdFromHeader(userIdHeader, authentication);

        log.info("User {} initiating deposit of {}", userId, request.getAmount());
        TransactionResponseDTO response = transactionService.createDeposit(request, userId, authHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Effectuer un retrait
     */
    @PostMapping("/withdrawal")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> withdrawal(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        request.setType(TransactionType.WITHDRAWAL);

        log.info("User {} initiating withdrawal of {}", userId, request.getAmount());
        TransactionResponseDTO response = transactionService.createWithdrawal(request, userId, authHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Effectuer un transfert
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransactionRequestDTO request,
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        request.setType(TransactionType.TRANSFER);

        log.info("User {} initiating transfer of {} to recipient {}",
                userId, request.getAmount(), request.getRecipientId());
        TransactionResponseDTO response = transactionService.createTransfer(request, userId, authHeader);

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
     * Récupérer les transactions d'un compte spécifique
     */
    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<TransactionResponseDTO>> getAccountTransactions(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        log.info("User {} retrieving transactions for account {}", userId, accountId);

        // TODO: Vérifier que l'utilisateur est propriétaire du compte
        List<TransactionResponseDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Récupérer une transaction par référence
     */
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> getByReference(
            @PathVariable String reference,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        log.info("User {} retrieving transaction by reference {}", userId, reference);

        TransactionResponseDTO transaction = transactionService.getTransactionByReference(reference);

        // Vérifier que la transaction appartient à l'utilisateur
        if (!transaction.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(transaction);
    }

    /**
     * Annuler une transaction (seulement si PENDING)
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionResponseDTO> cancelTransaction(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            Authentication authentication) {

        Long userId = getUserIdFromHeader(userIdHeader, authentication);
        log.info("User {} attempting to cancel transaction {}", userId, id);

        TransactionResponseDTO cancelled = transactionService.cancelTransaction(id, userId);
        return ResponseEntity.ok(cancelled);
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

        // Fallback: log que le header n'est pas présent
        log.warn("X-Auth-User-Id header not found. Authentication principal: {}",
                authentication != null ? authentication.getName() : "null");

        throw new RuntimeException("Unable to determine user ID. X-Auth-User-Id header is missing.");
    }
}
