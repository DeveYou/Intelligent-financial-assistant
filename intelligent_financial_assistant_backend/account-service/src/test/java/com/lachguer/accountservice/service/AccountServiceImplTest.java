package com.lachguer.accountservice.service;

import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.mapper.AccountMapper;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import com.lachguer.accountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Test
    void addAccount_shouldSaveCurrentAccount() {
        BankAccountRequestDTO requestDTO = new BankAccountRequestDTO();
        requestDTO.setType(AccountType.CURRENT_ACCOUNT);
        requestDTO.setBalance(1000.0);
        requestDTO.setOverDraft(200.0);
        requestDTO.setUserId(1L);

        CurrentAccount savedAccount = new CurrentAccount();
        savedAccount.setId(1L);
        savedAccount.setRib(UUID.randomUUID().toString());
        savedAccount.setBalance(1000.0);
        savedAccount.setCreatedAt(new Date());
        savedAccount.setIsActive(true);
        savedAccount.setUserId(1L);
        savedAccount.setOverDraft(200.0);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(AccountType.CURRENT_ACCOUNT);

        when(bankAccountRepository.save(any(CurrentAccount.class))).thenReturn(savedAccount);
        when(accountMapper.fromBankAccount(savedAccount)).thenReturn(responseDTO);

        BankAccountResponseDTO result = accountService.addAccount(requestDTO);

        assertNotNull(result);
        assertEquals(AccountType.CURRENT_ACCOUNT, result.getType());
    }

    @Test
    void addAccount_shouldSaveSavingAccount() {
        BankAccountRequestDTO requestDTO = new BankAccountRequestDTO();
        requestDTO.setType(AccountType.SAVING_ACCOUNT);
        requestDTO.setBalance(5000.0);
        requestDTO.setInterestRate(0.05);
        requestDTO.setUserId(1L);

        SavingAccount savedAccount = new SavingAccount();
        savedAccount.setId(2L);
        savedAccount.setRib(UUID.randomUUID().toString());
        savedAccount.setBalance(5000.0);
        savedAccount.setCreatedAt(new Date());
        savedAccount.setIsActive(true);
        savedAccount.setUserId(1L);
        savedAccount.setInterestRate(0.05);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setType(AccountType.SAVING_ACCOUNT);

        when(bankAccountRepository.save(any(SavingAccount.class))).thenReturn(savedAccount);
        when(accountMapper.fromBankAccount(savedAccount)).thenReturn(responseDTO);

        BankAccountResponseDTO result = accountService.addAccount(requestDTO);

        assertNotNull(result);
        assertEquals(AccountType.SAVING_ACCOUNT, result.getType());
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        Long accountId = 1L;
        Long userId = 1L;
        CurrentAccount account = new CurrentAccount();
        account.setId(accountId);
        account.setUserId(userId);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(accountId);

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.fromBankAccount(account)).thenReturn(responseDTO);

        BankAccountResponseDTO result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
    }

    @Test
    void getAccountById_shouldThrowExceptionWhenNotFound() {
        Long accountId = 1L;
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    void getAccounts_shouldReturnAllAccounts() {
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setUserId(1L);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setUserId(2L);

        when(bankAccountRepository.findAll()).thenReturn(java.util.List.of(currentAccount, savingAccount));
        when(accountMapper.fromBankAccount(any(BankAccount.class))).thenReturn(new BankAccountResponseDTO());

        java.util.List<BankAccountResponseDTO> result = accountService.getAccounts();

        assertEquals(2, result.size());
    }

    @Test
    void getAccountsByUserId_shouldReturnUserAccounts() {
        Long userId = 1L;
        CurrentAccount account = new CurrentAccount();
        account.setUserId(userId);
        when(bankAccountRepository.findByUserId(userId)).thenReturn(java.util.List.of(account));
        when(accountMapper.fromBankAccount(any(BankAccount.class))).thenReturn(new BankAccountResponseDTO());

        java.util.List<BankAccountResponseDTO> result = accountService.getAccountsByUserId(userId);

        assertEquals(1, result.size());
    }

    @Test
    void updateAccount_shouldUpdateCurrentAccount() {
        Long accountId = 1L;
        CurrentAccount existingAccount = new CurrentAccount();
        existingAccount.setId(accountId);
        existingAccount.setBalance(1000.0);
        existingAccount.setOverDraft(200.0);

        CurrentAccount updatedInfo = new CurrentAccount();
        updatedInfo.setBalance(1500.0);
        updatedInfo.setOverDraft(300.0);

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        BankAccount result = accountService.updateAccount(accountId, updatedInfo);

        assertEquals(1500.0, result.getBalance());
        assertEquals(300.0, ((CurrentAccount) result).getOverDraft());
    }

    @Test
    void updateAccount_shouldThrowExceptionWhenNotFound() {
        Long accountId = 1L;
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.updateAccount(accountId, new CurrentAccount()));
    }

    @Test
    void deleteAccount_shouldCallRepositoryDelete() {
        Long accountId = 1L;
        accountService.deleteAccount(accountId);
        org.mockito.Mockito.verify(bankAccountRepository, org.mockito.Mockito.times(1)).deleteById(accountId);
    }
}
