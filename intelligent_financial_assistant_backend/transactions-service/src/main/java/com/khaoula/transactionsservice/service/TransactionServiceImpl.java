package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.client.AccountClient;
import com.khaoula.transactionsservice.client.RecipientClient;
import com.khaoula.transactionsservice.client.UserClient;
import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.*;
import com.khaoula.transactionsservice.exception.InsufficientBalanceException;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.exception.ResourceNotFoundException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final RecipientClient recipientClient;
    private final UserClient userClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountClient accountClient,
            RecipientClient recipientClient, UserClient userClient) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
        this.recipientClient = recipientClient;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public TransactionResponseDTO createDeposit(TransactionRequestDTO request, String authHeader) {
        log.info("Creating deposit for account: {}, amount: {}", request.getBankAccountId(), request.getAmount());

        // Valider le compte
        AccountClient.AccountResponse account = accountClient.getAccountById(request.getBankAccountId(), authHeader);

        if (!account.getIsActive()) {
            throw new InvalidTransactionException("Account is not active");
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(account.getUserId());
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
                    authHeader);

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
    public TransactionResponseDTO createWithdrawal(TransactionRequestDTO request, String authHeader) {
        log.info("Creating withdrawal for account: {}, amount: {}", request.getBankAccountId(), request.getAmount());

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
        transaction.setUserId(account.getUserId());
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
                    authHeader);

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
    public TransactionResponseDTO createTransfer(TransferRequestDTO request, String authHeader) {
        log.info("Creating transfer for account: {}, amount: {}", request.getBankAccountId(), request.getAmount());

        AccountClient.AccountResponse sourceAccount = accountClient.getAccountById(request.getBankAccountId(),
                authHeader);

        if (!sourceAccount.getIsActive()) {
            throw new InvalidTransactionException("Source account is not active");
        }

        // Vérifier le solde
        if (sourceAccount.getBalance() < request.getAmount().doubleValue()) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        RecipientClient.RecipientResponse recipient;
        String recipientIban;

        if (request.getRecipientIban() != null) {
            // Utiliser directement l'IBAN fourni
            recipientIban = request.getRecipientIban();

            log.info("Fetching recipient by IBAN: {}", recipientIban);
            try {
                RecipientClient.ApiResponse<RecipientClient.RecipientResponse> response = recipientClient
                        .getRecipientByIban(recipientIban, authHeader);
                recipient = response.getData();
            } catch (Exception e) {
                log.info("Recipient not found for IBAN: {}. Attempting to auto-create.", recipientIban);
                try {
                    // Check if it's a valid internal account
                    AccountClient.AccountResponse accountResponse = accountClient.getAccountByIban(recipientIban,
                            authHeader);

                    // Fetch user details
                    UserClient.UserDetails userDetails = userClient.getUserById(accountResponse.getUserId(),
                            authHeader);

                    // Create new recipient
                    RecipientRequest recipientRequest = new RecipientRequest(
                            userDetails.getFirstName() + " " + userDetails.getLastName(),
                            recipientIban,
                            "Internal Bank Ent");

                    RecipientClient.ApiResponse<RecipientClient.RecipientResponse> createResponse = recipientClient
                            .addRecipient(recipientRequest, authHeader);
                    recipient = createResponse.getData();

                } catch (Exception ex) {
                    log.warn(
                            "Failed to auto-create recipient: {}. Proceeding with transaction without linking to a recipient profile.",
                            ex.getMessage());
                    recipient = null;
                }
            }
        } else {
            throw new InvalidTransactionException("Recipient IBAN is required for transfer");
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(sourceAccount.getUserId());
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setReference(generateReference());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(request.getAmount());

        // Stocker l'ID du bénéficiaire si disponible, sinon stocker l'IBAN
        if (recipient != null) {
            transaction.setRecipientId(recipient.getId());
            transaction.setRecipientName(recipient.getFullName());
            transaction.setRecipientIban(recipient.getIban());
        } else {
            transaction.setRecipientId(null);
            transaction.setRecipientName(null);
            transaction.setRecipientIban(request.getRecipientIban());
        }

        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());

        transaction = transactionRepository.save(transaction);

        try {
            accountClient.updateBalance(
                    sourceAccount.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "SUBTRACT"),
                    authHeader);

            AccountClient.AccountResponse accountResponse = accountClient.getAccountByIban(request.getRecipientIban(),
                    authHeader);
            accountClient.updateBalance(
                    accountResponse.getId(),
                    new AccountClient.BalanceUpdateRequest(request.getAmount().doubleValue(), "ADD"),
                    authHeader);

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
                filter.getSortBy());

        Page<Transaction> transactions = transactionRepository.findByFilters(
                filter.getUserId(),
                filter.getBankAccountId(),
                filter.getType(),
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable);

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

        Double totalVolume = transactionRepository.sumAmountByStatus(TransactionStatus.COMPLETED);
        if (totalVolume == null) {
            totalVolume = 0.0;
        }

        OffsetDateTime startOfDay = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime endOfDay = OffsetDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        Long todayTransactions = transactionRepository.countByDateBetween(startOfDay, endOfDay);

        return new TransactionStatsDTO(total, pending, completed, failed, totalVolume, todayTransactions);
    }

    @Override
    public List<DailyTransactionStats> getDailyStats() {
        OffsetDateTime sevenDaysAgo = OffsetDateTime.now().minusDays(7);
        return transactionRepository.findDailyStats(sevenDaysAgo);
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
        dto.setRecipientName(transaction.getRecipientName());
        dto.setRecipientIban(transaction.getRecipientIban());
        dto.setReason(transaction.getReason());
        dto.setDate(transaction.getDate());
        return dto;
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction,
            RecipientClient.RecipientResponse recipient) {
        TransactionResponseDTO dto = mapToResponseDTO(transaction);
        if (recipient != null) {
            dto.setRecipientName(recipient.getFullName());
            dto.setRecipientIban(recipient.getIban());
        }
        return dto;
    }
}
