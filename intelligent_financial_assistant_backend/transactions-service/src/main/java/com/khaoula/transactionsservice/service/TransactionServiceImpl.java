package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.exception.DuplicateReferenceException;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionResponseDTO deposit(DepositRequestDTO request) {
        validateAmount(request.getAmount());

        Transaction transaction = new Transaction();
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());

        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }

    @Override
    public TransactionResponseDTO withdraw(WithdrawalRequestDTO request) {
        validateAmount(request.getAmount());

        Transaction transaction = new Transaction();
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());

        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }

    @Override
    public TransactionResponseDTO transfer(TransferRequestDTO request) {
        validateAmount(request.getAmount());

        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new InvalidTransactionException("Source and target accounts must be different for a transfer");
        }

        Transaction transaction = new Transaction();
        transaction.setBankAccountId(request.getSourceAccountId());
        transaction.setReceiver(request.getTargetAccountId());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setReason(request.getReason());
        transaction.setDate(OffsetDateTime.now());
        transaction.setReference(generateUniqueReference());

        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getHistoryByAccount(String bankAccountId) {
        return transactionRepository.findByBankAccountIdOrderByDateDesc(bankAccountId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
        dto.setAmount(transaction.getAmount());
        dto.setReceiver(transaction.getReceiver());
        dto.setReason(transaction.getReason());
        dto.setDate(transaction.getDate());
        return dto;
    }
}

