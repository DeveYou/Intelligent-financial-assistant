package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.client.AuthClient;
import com.khaoula.transactionsservice.client.BankAccountClient;
import com.khaoula.transactionsservice.client.RecipientClient;
import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.BankAccountDTO;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.dto.UserDTO;
import com.khaoula.transactionsservice.dto.RecipientDTO;
import com.khaoula.transactionsservice.exception.DuplicateReferenceException;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthClient authClient;
    private final BankAccountClient bankAccountClient;
    private final RecipientClient recipientClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AuthClient authClient, BankAccountClient bankAccountClient, RecipientClient recipientClient) {
        this.transactionRepository = transactionRepository;
        this.authClient = authClient;
        this.bankAccountClient = bankAccountClient;
        this.recipientClient = recipientClient;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionResponseDTO deposit(Long authenticatedUserId, DepositRequestDTO request) {
        validateAmount(request.getAmount());

        UserDTO user = authClient.getUserById(authenticatedUserId);
        if (user == null) {
            throw new InvalidTransactionException("User not found with ID: " + authenticatedUserId);
        }

        BankAccountDTO account = bankAccountClient.getAccountByRib(request.getBankAccountId());
        if (account == null || !account.isActive() || !account.getUserId().equals(authenticatedUserId)) {
            throw new InvalidTransactionException("Bank account not found, inactive, or does not belong to the user: " + request.getBankAccountId());
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(authenticatedUserId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);

        try {
            bankAccountClient.deposit(request);
            saved.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e) {
            saved.setStatus(TransactionStatus.FAILED);
            // Optionally log the exception e
        }

        return toDto(transactionRepository.save(saved));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionResponseDTO withdraw(Long authenticatedUserId, WithdrawalRequestDTO request) {
        validateAmount(request.getAmount());

        UserDTO user = authClient.getUserById(authenticatedUserId);
        if (user == null) {
            throw new InvalidTransactionException("User not found with ID: " + authenticatedUserId);
        }

        BankAccountDTO account = bankAccountClient.getAccountByRib(request.getBankAccountId());
        if (account == null || !account.isActive() || !account.getUserId().equals(authenticatedUserId)) {
            throw new InvalidTransactionException("Bank account not found, inactive, or does not belong to the user: " + request.getBankAccountId());
        }

        if (account.getBalance() == null || account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient funds for account: " + request.getBankAccountId());
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(authenticatedUserId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);

        try {
            bankAccountClient.withdraw(request);
            saved.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e) {
            saved.setStatus(TransactionStatus.FAILED);
        }

        return toDto(transactionRepository.save(saved));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionResponseDTO transfer(Long authenticatedUserId, TransferRequestDTO request) {
        validateAmount(request.getAmount());

        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new InvalidTransactionException("Source and target accounts must be different for a transfer");
        }

        UserDTO user = authClient.getUserById(authenticatedUserId);
        if (user == null) {
            throw new InvalidTransactionException("Authenticated user not found with ID: " + authenticatedUserId);
        }

        BankAccountDTO source = bankAccountClient.getAccountByRib(request.getSourceAccountId());
        BankAccountDTO target = bankAccountClient.getAccountByRib(request.getTargetAccountId());

        // Si la target n'existe pas côté bank-account, tenter de la résoudre via recipient-service (IBAN)
        boolean targetResolvedViaRecipient = false;
        if (target == null) {
            RecipientDTO recipient = null;
            try {
                recipient = recipientClient.getByIban(request.getTargetAccountId());
            } catch (Exception e) {
                // ignore, we'll fail later if not resolvable
            }
            if (recipient != null) {
                // marquer receiver avec l'IBAN/nom selon besoin
                request.setTargetAccountId(recipient.getIban());
                targetResolvedViaRecipient = true;
            }
        }

        if (source == null || !source.isActive()) {
            throw new InvalidTransactionException("Source account not found or inactive: " + request.getSourceAccountId());
        }
        if (target == null && !targetResolvedViaRecipient) {
            // après tentative recipient, toujours null => erreur
            throw new InvalidTransactionException("Target account not found or inactive: " + request.getTargetAccountId());
        }
        if (target != null && !target.isActive()) {
            throw new InvalidTransactionException("Target account not found or inactive: " + request.getTargetAccountId());
        }

        if (!source.getUserId().equals(authenticatedUserId)) {
            throw new InvalidTransactionException("Source account does not belong to the authenticated user");
        }

        if (source.getBalance() == null || source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient funds in source account: " + request.getSourceAccountId());
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(authenticatedUserId);
        transaction.setBankAccountId(request.getSourceAccountId());
        // Si la target a été résolue via recipient-service, on stocke son id
        if (targetResolvedViaRecipient) {
            RecipientDTO recipient = recipientClient.getByIban(request.getTargetAccountId());
            if (recipient != null && recipient.getId() != null) {
                transaction.setRecipientId(recipient.getId());
            }
        } else {
            // transferts vers un autre account : recipientId non renseigné
            transaction.setRecipientId(null);
        }
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);

        try {
            bankAccountClient.transfer(request);
            saved.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e) {
            saved.setStatus(TransactionStatus.FAILED);
        }

        return toDto(transactionRepository.save(saved));
    }

    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<TransactionResponseDTO> search(
            String type, String bankAccountId, String reference,
            String search, OffsetDateTime startDate, OffsetDateTime endDate
    ) {
        TransactionType transactionType = type != null ? TransactionType.valueOf(type.toUpperCase()) : null;
        return transactionRepository.searchTransactions(
                        transactionType, bankAccountId, reference, search, startDate, endDate
                ).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<TransactionResponseDTO> getHistoryByAccount(String bankAccountId) {
        return transactionRepository.findByBankAccountIdOrderByDateDesc(bankAccountId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionResponseDTO getByReference(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with reference: " + reference));
        return toDto(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }
    }

    private String generateUniqueReference() {
        String ref;
        do {
            ref = UUID.randomUUID().toString();
        } while (transactionRepository.findByReference(ref).isPresent());

        return ref;
    }

    private TransactionResponseDTO toDto(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
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
}
