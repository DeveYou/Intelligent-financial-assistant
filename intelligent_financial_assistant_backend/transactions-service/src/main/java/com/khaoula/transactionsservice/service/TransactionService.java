package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO deposit(DepositRequestDTO request);

    TransactionResponseDTO withdraw(WithdrawalRequestDTO request);

    TransactionResponseDTO transfer(TransferRequestDTO request);

    List<TransactionResponseDTO> getHistoryByAccount(String bankAccountId);

    TransactionResponseDTO getByReference(String reference);
}
