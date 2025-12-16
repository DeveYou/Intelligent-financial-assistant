package com.lachguer.accountservice.service;

import com.lachguer.accountservice.client.AuthRestClient;
import com.lachguer.accountservice.client.TransactionRestClient;
import com.lachguer.accountservice.dto.*;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.mapper.AccountMapper;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import com.lachguer.accountservice.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.lachguer.accountservice.model.User;
import org.springframework.beans.BeanUtils;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private BankAccountRepository bankAccountRepository;
    private AccountMapper accountMapper;
    private TransactionRestClient transactionRestClient;
    private AuthRestClient authRestClient;

    @Override
    public BankAccountResponseDTO addAccount(BankAccountRequestDTO bankAccountDTO) {
        List<BankAccount> existingAccounts = bankAccountRepository.findByUserId(bankAccountDTO.getUserId());

        if (existingAccounts.size() >= 2) {
            throw new RuntimeException("User already has maximum number of accounts (2)");
        }

        boolean hasSameType = existingAccounts.stream()
                .anyMatch(acc -> {
                    if (bankAccountDTO.getType() == AccountType.CURRENT_ACCOUNT) {
                        return acc instanceof CurrentAccount;
                    } else {
                        return acc instanceof SavingAccount;
                    }
                });

        if (hasSameType) {
            throw new RuntimeException("User already has an account of type " + bankAccountDTO.getType());
        }

        BankAccount bankAccount;
        if (bankAccountDTO.getType() == AccountType.CURRENT_ACCOUNT) {
            bankAccount = new CurrentAccount();
            ((CurrentAccount) bankAccount).setOverDraft(1000.0);
        } else {
            bankAccount = new SavingAccount();
            ((SavingAccount) bankAccount).setInterestRate(3.5);
        }
        bankAccount.setAccountType(bankAccountDTO.getType());

        StringBuilder ribBuilder = new StringBuilder();
        java.util.Random random = new Random();
        for (int i = 0; i < 16; i++) {
            ribBuilder.append(random.nextInt(10));
        }
        bankAccount.setRib(ribBuilder.toString());

        bankAccount.setBalance(0.0);

        Date now = new Date();
        bankAccount.setCreatedAt(now);

        bankAccount.setExpirationDate(LocalDate.now().plusYears(4));

        bankAccount.setIsPaymentByCard(false);
        bankAccount.setIsWithdrawal(false);
        bankAccount.setIsOnlinePayment(false);
        bankAccount.setIsContactless(false);
        bankAccount.setIsActive(true);
        bankAccount.setUserId(bankAccountDTO.getUserId());

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return accountMapper.fromBankAccount(savedAccount);
    }

    @Override
    public BankAccountResponseDTO getAccountById(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id=" + id));

        // enrichAccountWithUser(bankAccount);

        return accountMapper.fromBankAccount(bankAccount);
    }

    @Override
    public BankAccountResponseDTO getAccountByIBAN(String iban) {
        BankAccount bankAccount = bankAccountRepository.findByRib(iban);

        // enrichAccountWithUser(bankAccount);

        return accountMapper.fromBankAccount(bankAccount);
    }

    @Override
    public List<BankAccountResponseDTO> getAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        bankAccounts.forEach(this::enrichAccountWithUser);
        return bankAccounts.stream()
                .map(accountMapper::fromBankAccount)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<BankAccountResponseDTO> getAccountsByUserId(Long userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);
        bankAccounts.forEach(this::enrichAccountWithUser);
        return bankAccounts.stream()
                .map(accountMapper::fromBankAccount)
                .collect(java.util.stream.Collectors.toList());
    }

    private void enrichAccountWithUser(BankAccount bankAccount) {
        if (bankAccount.getUserId() != null) {
            try {
                UserDTO userDTO = authRestClient.getUserById(bankAccount.getUserId());
                if (userDTO != null) {
                    User user = new User();
                    BeanUtils.copyProperties(userDTO, user);
                    bankAccount.setUser(user);
                }
            } catch (Exception e) {
                // Log error but don't fail the request if auth service is down
                System.err.println("Error fetching user for account " + bankAccount.getId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public BankAccount updateAccount(Long id, BankAccountUpdateDTO updateDTO) {
        BankAccount accountToUpdate = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (updateDTO.getBalance() != null)
            accountToUpdate.setBalance(updateDTO.getBalance());
        if (updateDTO.getIsActive() != null)
            accountToUpdate.setIsActive(updateDTO.getIsActive());
        if (updateDTO.getIsContactless() != null)
            accountToUpdate.setIsContactless(updateDTO.getIsContactless());
        if (updateDTO.getIsWithdrawal() != null)
            accountToUpdate.setIsWithdrawal(updateDTO.getIsWithdrawal());
        if (updateDTO.getIsPaymentByCard() != null)
            accountToUpdate.setIsPaymentByCard(updateDTO.getIsPaymentByCard());
        if (updateDTO.getIsOnlinePayment() != null)
            accountToUpdate.setIsOnlinePayment(updateDTO.getIsOnlinePayment());

        if (accountToUpdate instanceof CurrentAccount && updateDTO.getOverDraft() != null) {
            ((CurrentAccount) accountToUpdate).setOverDraft(updateDTO.getOverDraft());
        }
        if (accountToUpdate instanceof SavingAccount && updateDTO.getInterestRate() != null) {
            ((SavingAccount) accountToUpdate).setInterestRate(updateDTO.getInterestRate());
        }
        return bankAccountRepository.save(accountToUpdate);
    }

    @Override
    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }

    // Méthode pour valider le token avant chaque opération sensible
    private void validateToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new com.lachguer.accountservice.exception.UnauthorizedException("Token d'autorisation manquant");
        }
        Boolean isValid = authRestClient.validateToken(authorizationHeader);
        if (!isValid) {
            throw new com.lachguer.accountservice.exception.UnauthorizedException("Token invalide ou expiré");
        }
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsForAccount(Long accountId, String authorizationHeader) {
        validateToken(authorizationHeader); // Valider le token
        bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        return transactionRestClient.getTransactionsByAccount(accountId, authorizationHeader);
    }

    @Override
    public TransactionResponseDTO createTransactionForAccount(Long accountId, TransactionRequestDTO request,
            String authorizationHeader) {
        validateToken(authorizationHeader); // Valider le token
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        request.setAccountId(accountId);
        request.setUserId(account.getUserId());
        return transactionRestClient.createTransaction(request, authorizationHeader);
    }

    @Override
    public long countUsers() {
        return bankAccountRepository.count();
    }

    @Override
    public List<AccountDistributionDTO> getAccountDistribution() {
        List<Object[]> results = bankAccountRepository.countTotalAccountsByType();
        return results.stream()
                .map(result -> new AccountDistributionDTO((AccountType) result[0], (Long) result[1]))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void updateBalance(Long accountId, Double amount, String operation) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if ("ADD".equals(operation)) {
            account.setBalance(account.getBalance() + amount);
        } else if ("SUBTRACT".equals(operation)) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

        bankAccountRepository.save(account);
    }
}
