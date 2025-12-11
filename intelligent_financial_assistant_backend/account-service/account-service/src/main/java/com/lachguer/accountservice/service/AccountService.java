package com.lachguer.accountservice.service;

import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.dto.TransactionRequestDTO;
import com.lachguer.accountservice.dto.TransactionResponseDTO;
import com.lachguer.accountservice.model.BankAccount;

import java.util.List;

public interface AccountService {
    BankAccountResponseDTO addAccount(BankAccountRequestDTO bankAccountDTO);
    BankAccountResponseDTO getAccountById(Long id);
    List<BankAccountResponseDTO> getAccounts();
    List<BankAccountResponseDTO> getAccountsByUserId(Long userId);
    BankAccount updateAccount(Long id, BankAccount bankAccount);
    void deleteAccount(Long id);

    // Transaction operations via TRANSACTION-SERVICE
    List<TransactionResponseDTO> getTransactionsForAccount(Long accountId, String authorizationHeader);
    TransactionResponseDTO createTransactionForAccount(Long accountId, TransactionRequestDTO request, String authorizationHeader);
}

