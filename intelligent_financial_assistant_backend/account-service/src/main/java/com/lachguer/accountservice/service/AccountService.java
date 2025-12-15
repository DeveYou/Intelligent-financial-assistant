package com.lachguer.accountservice.service;

import com.lachguer.accountservice.dto.*;
import com.lachguer.accountservice.model.BankAccount;

import java.util.List;

public interface AccountService {
    BankAccountResponseDTO addAccount(BankAccountRequestDTO bankAccountDTO);

    BankAccountResponseDTO getAccountById(Long id);
    BankAccountResponseDTO getAccountByIBAN(String iban);

    List<BankAccountResponseDTO> getAccounts();

    List<BankAccountResponseDTO> getAccountsByUserId(Long userId);

    BankAccount updateAccount(Long id, BankAccountUpdateDTO bankAccount);

    void deleteAccount(Long id);

    // Transaction operations via TRANSACTION-SERVICE
    List<TransactionResponseDTO> getTransactionsForAccount(Long accountId, String authorizationHeader);

    TransactionResponseDTO createTransactionForAccount(Long accountId, TransactionRequestDTO request, String authorizationHeader);

    long countUsers();

    void updateBalance(Long accountId, Double amount, String operation);
}

