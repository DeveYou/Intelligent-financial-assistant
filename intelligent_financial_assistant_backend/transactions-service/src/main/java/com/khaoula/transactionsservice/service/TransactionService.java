package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.dto.*;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {

    /**
     * Créer un dépôt
     */
    TransactionResponseDTO createDeposit(TransactionRequestDTO request, String authHeader);

    /**
     * Créer un retrait
     */
    TransactionResponseDTO createWithdrawal(TransactionRequestDTO request, String authHeader);

    /**
     * Créer un transfert
     */
    TransactionResponseDTO createTransfer(TransferRequestDTO request, String authHeader);

    /**
     * Récupérer toutes les transactions (Admin)
     */
    Page<TransactionResponseDTO> getAllTransactions(TransactionFilterDTO filter);

    /**
     * Récupérer les transactions d'un utilisateur
     */
    List<TransactionResponseDTO> getUserTransactions(Long userId);

    /**
     * Récupérer les transactions d'un compte bancaire
     */
    List<TransactionResponseDTO> getAccountTransactions(Long bankAccountId);

    /**
     * Récupérer une transaction par son ID
     */
    TransactionResponseDTO getTransactionById(Long id);

    /**
     * Récupérer une transaction par sa référence
     */
    TransactionResponseDTO getTransactionByReference(String reference);

    /**
     * Annuler une transaction (si PENDING)
     */
    TransactionResponseDTO cancelTransaction(Long id, Long userId);

    /**
     * Statistiques (Admin)
     */
    TransactionStatsDTO getTransactionStats();

    @Data
    class TransactionStatsDTO {
        private Long totalTransactions;
        private Long pendingTransactions;
        private Long completedTransactions;
        private Long failedTransactions;

        public TransactionStatsDTO(Long total, Long pending, Long completed, Long failed) {
            this.totalTransactions = total;
            this.pendingTransactions = pending;
            this.completedTransactions = completed;
            this.failedTransactions = failed;
        }

    }
}