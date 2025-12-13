package com.lachguer.accountservice.service;

import com.lachguer.accountservice.client.AuthRestClient;
import com.lachguer.accountservice.client.TransactionRestClient;
import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.dto.TransactionRequestDTO;
import com.lachguer.accountservice.dto.TransactionResponseDTO;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.mapper.AccountMapper;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import com.lachguer.accountservice.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lachguer.accountservice.dto.UserDTO;
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
        BankAccount bankAccount;
        if (bankAccountDTO.getType() == AccountType.CURRENT_ACCOUNT) {
            bankAccount = new CurrentAccount();
            ((CurrentAccount) bankAccount).setOverDraft(bankAccountDTO.getOverDraft());
        } else {
            bankAccount = new SavingAccount();
            ((SavingAccount) bankAccount).setInterestRate(bankAccountDTO.getInterestRate());
        }
        
        
        bankAccount.setRib(bankAccountDTO.getIban());
        
        bankAccount.setBalance(0.0);
        
        Date now = new Date();
        bankAccount.setCreatedAt(now);
        
        
        bankAccount.setExpirationDate(bankAccountDTO.getExpirationDate());

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
        
        enrichAccountWithUser(bankAccount);
        
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
    public BankAccount updateAccount(Long id, BankAccount bankAccount) {
        BankAccount accountToUpdate = bankAccountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if (bankAccount.getBalance() != null) accountToUpdate.setBalance(bankAccount.getBalance());
        if (bankAccount.getIsActive() != null) accountToUpdate.setIsActive(bankAccount.getIsActive());
        if (bankAccount instanceof CurrentAccount && accountToUpdate instanceof CurrentAccount) {
            ((CurrentAccount) accountToUpdate).setOverDraft(((CurrentAccount) bankAccount).getOverDraft());
        }
        if (bankAccount instanceof SavingAccount && accountToUpdate instanceof SavingAccount) {
            ((SavingAccount) accountToUpdate).setInterestRate(((SavingAccount) bankAccount).getInterestRate());
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
    public TransactionResponseDTO createTransactionForAccount(Long accountId, TransactionRequestDTO request, String authorizationHeader) {
        validateToken(authorizationHeader); // Valider le token
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        request.setAccountId(accountId);
        request.setUserId(account.getUserId());
        return transactionRestClient.createTransaction(request, authorizationHeader);
    }

    @Override
    public long countUsers() {
        return bankAccountRepository.count();
    }
}
