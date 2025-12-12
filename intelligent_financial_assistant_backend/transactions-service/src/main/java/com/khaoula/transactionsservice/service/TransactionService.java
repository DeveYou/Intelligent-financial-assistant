package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionService {
    TransactionResponseDTO deposit(Long authenticatedUserId, DepositRequestDTO request);
    TransactionResponseDTO withdraw(Long authenticatedUserId, WithdrawalRequestDTO request);
    TransactionResponseDTO transfer(Long authenticatedUserId, TransferRequestDTO request);
    List<TransactionResponseDTO> getHistoryByAccount(String bankAccountId);
    TransactionResponseDTO getByReference(String reference);
    List<TransactionResponseDTO> search(
            String type,
            String bankAccountId,
            String reference,
            String search,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );
}

