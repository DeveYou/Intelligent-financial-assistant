package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.client.AccountClient;
import com.khaoula.transactionsservice.client.RecipientClient;
import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.*;
import com.khaoula.transactionsservice.exception.InsufficientBalanceException;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.exception.ResourceNotFoundException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final RecipientClient recipientClient;


    @Override
    @Transactional
    public TransactionResponseDTO createDeposit(TransactionRequestDTO request, Long userId, String authHeader) {
        log.info("Creating deposit for user: {}, amount: {}", userId, request.getAmount());

        // Valider le compte
        AccountClient.AccountResponse account = accountClient.getAccountById(request.getBankAccountId(), authHeader);

        if (!account.getIsActive()) {
            throw new InvalidTransactionException("Account is not active");
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setReference(generateReference());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());

        // Sauvegarder
        transaction = transactionRepository.save(transaction);

        try {
            // Mettre à jour le solde
            accountClient.updateBalance(
                    account.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "ADD"),
                    authHeader
            );

            // Marquer comme complétée
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            log.info("Deposit completed successfully: {}", transaction.getReference());
        } catch (Exception e) {
            log.error("Failed to complete deposit: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Failed to process deposit: " + e.getMessage());
        }

        return mapToResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO createWithdrawal(TransactionRequestDTO request, Long userId, String authHeader) {
        log.info("Creating withdrawal for user: {}, amount: {}", userId, request.getAmount());

        // Valider le compte
        AccountClient.AccountResponse account = accountClient.getAccountById(request.getBankAccountId(), authHeader);

        if (!account.getIsActive()) {
            throw new InvalidTransactionException("Account is not active");
        }

        // Vérifier le solde
        if (account.getBalance() < request.getAmount().doubleValue()) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setReference(generateReference());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());

        transaction = transactionRepository.save(transaction);

        try {
            // Mettre à jour le solde
            accountClient.updateBalance(
                    account.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "SUBTRACT"),
                    authHeader
            );

            // Marquer comme complétée
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            log.info("Withdrawal completed successfully: {}", transaction.getReference());
        } catch (Exception e) {
            log.error("Failed to complete withdrawal: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Failed to process withdrawal: " + e.getMessage());
        }

        return mapToResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO createTransfer(TransferRequestDTO request, Long userId, String authHeader) {
        log.info("Creating transfer for user: {}, amount: {}", userId, request.getAmount());

        AccountClient.AccountResponse sourceAccount = accountClient.getAccountById(request.getBankAccountId(), authHeader);

        if (!sourceAccount.getIsActive()) {
            throw new InvalidTransactionException("Source account is not active");
        }

        if (sourceAccount.getBalance() < request.getAmount().doubleValue()) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        RecipientClient.RecipientResponse recipient;
        String recipientIban;

        if (request.getRecipientIban() != null) {
            recipientIban = request.getRecipientIban();

            log.info("Fetching recipient by IBAN: {}", recipientIban);
            RecipientClient.ApiResponse<RecipientClient.RecipientResponse> response =
                    recipientClient.getRecipientByIban(recipientIban, authHeader);
            recipient = response.getData();
        } else {
            throw new InvalidTransactionException("Recipient IBAN is required for transfer");
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setReference(generateReference());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(request.getAmount());

        if (recipient != null) {
            transaction.setRecipientId(recipient.getId());
        } else {
            transaction.setRecipientId(null);
        }

        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());

        transaction = transactionRepository.save(transaction);

        try {
            accountClient.updateBalance(
                    sourceAccount.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "SUBTRACT"),
                    authHeader
            );

            AccountClient.AccountResponse accountResponse = accountClient.getAccountByIban(request.getRecipientIban(), authHeader);
            accountClient.updateBalance(
                    accountResponse.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "ADD"),
                    authHeader
            );

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            log.info("Transfer completed successfully: {} to IBAN: {}",
                    transaction.getReference(), recipientIban);
        } catch (Exception e) {
            log.error("Failed to complete transfer: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Failed to process transfer: " + e.getMessage());
        }

        return mapToResponseDTO(transaction, recipient);
    }

    @Override
    public Page<TransactionResponseDTO> getAllTransactions(TransactionFilterDTO filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.Direction.fromString(filter.getSortDirection()),
                filter.getSortBy()
        );

        Page<Transaction> transactions = transactionRepository.findByFilters(
                filter.getUserId(),
                filter.getBankAccountId(),
                filter.getType(),
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );

        return transactions.map(this::mapToResponseDTO);
    }

    @Override
    public List<TransactionResponseDTO> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<TransactionResponseDTO> getAccountTransactions(Long bankAccountId) {
        List<Transaction> transactions = transactionRepository.findByBankAccountId(bankAccountId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return mapToResponseDTO(transaction);
    }

    @Override
    public TransactionResponseDTO getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + reference));
        return mapToResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO cancelTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        if (!transaction.getUserId().equals(userId)) {
            throw new InvalidTransactionException("You are not authorized to cancel this transaction");
        }

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new InvalidTransactionException("Only pending transactions can be cancelled");
        }

        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction = transactionRepository.save(transaction);

        log.info("Transaction cancelled: {}", transaction.getReference());
        return mapToResponseDTO(transaction);
    }

    @Override
    public TransactionStatsDTO getTransactionStats() {
        Long total = transactionRepository.count();
        Long pending = transactionRepository.countByStatus(TransactionStatus.PENDING);
        Long completed = transactionRepository.countByStatus(TransactionStatus.COMPLETED);
        Long failed = transactionRepository.countByStatus(TransactionStatus.FAILED);

        return new TransactionStatsDTO(total, pending, completed, failed);
    }

    // Helper methods
    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setBankAccountId(transaction.getBankAccountId());
        dto.setReference(transaction.getReference());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setAmount(transaction.getAmount());
        dto.setRecipientId(transaction.getRecipientId());
        dto.setReason(transaction.getReason());
        dto.setDate(transaction.getDate());
        return dto;
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction, RecipientClient.RecipientResponse recipient) {
        TransactionResponseDTO dto = mapToResponseDTO(transaction);
        if (recipient != null) {
            dto.setRecipientIban(recipient.getIban());
        }
        return dto;
    }
}
