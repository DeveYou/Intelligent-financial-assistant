package com.lachguer.accountservice.service;

import com.lachguer.accountservice.client.AuthRestClient;
import com.lachguer.accountservice.client.TransactionRestClient;
import com.lachguer.accountservice.dto.*;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.mapper.AccountMapper;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.model.SavingAccount;
import com.lachguer.accountservice.model.User;
import com.lachguer.accountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionRestClient transactionRestClient;

    @Mock
    private AuthRestClient authRestClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    private BankAccountRequestDTO bankAccountRequestDTO;
    private BankAccount bankAccount;
    private BankAccountResponseDTO bankAccountResponseDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        bankAccountRequestDTO = new BankAccountRequestDTO();
        bankAccountRequestDTO.setUserId(1L);
        bankAccountRequestDTO.setType(AccountType.CURRENT_ACCOUNT);

        bankAccount = new CurrentAccount();
        bankAccount.setId(1L);
        bankAccount.setUserId(1L);
        bankAccount.setAccountType(AccountType.CURRENT_ACCOUNT);
        bankAccount.setBalance(1000.0);
        bankAccount.setRib("1234567890123456");

        bankAccountResponseDTO = new BankAccountResponseDTO();
        bankAccountResponseDTO.setId(1L);
        bankAccountResponseDTO.setUserId(1L);
        bankAccountResponseDTO.setType(AccountType.CURRENT_ACCOUNT);
        bankAccountResponseDTO.setBalance(1000.0);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstname("John");
        userDTO.setLastname("Doe");
    }

    @Test
    void addAccount_Success() {
        when(bankAccountRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);
        when(accountMapper.fromBankAccount(any(BankAccount.class))).thenReturn(bankAccountResponseDTO);

        BankAccountResponseDTO result = accountService.addAccount(bankAccountRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void addAccount_SavingAccount_Success() {
        bankAccountRequestDTO.setType(AccountType.SAVING_ACCOUNT);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(2L);
        savingAccount.setUserId(1L);
        savingAccount.setAccountType(AccountType.SAVING_ACCOUNT);

        when(bankAccountRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savingAccount);
        when(accountMapper.fromBankAccount(any(BankAccount.class))).thenReturn(new BankAccountResponseDTO());

        BankAccountResponseDTO result = accountService.addAccount(bankAccountRequestDTO);
        assertNotNull(result);
    }

    @Test
    void addAccount_MaxAccountsReached() {
        List<BankAccount> existingAccounts = Arrays.asList(new CurrentAccount(), new SavingAccount());
        when(bankAccountRepository.findByUserId(1L)).thenReturn(existingAccounts);

        assertThrows(RuntimeException.class, () -> accountService.addAccount(bankAccountRequestDTO));
    }

    @Test
    void addAccount_SameTypeExists() {
        List<BankAccount> existingAccounts = Collections.singletonList(new CurrentAccount());
        when(bankAccountRepository.findByUserId(1L)).thenReturn(existingAccounts);

        assertThrows(RuntimeException.class, () -> accountService.addAccount(bankAccountRequestDTO));
    }

    @Test
    void addAccount_SameSavingTypeExists() {
        bankAccountRequestDTO.setType(AccountType.SAVING_ACCOUNT);
        List<BankAccount> existingAccounts = Collections.singletonList(new SavingAccount());
        when(bankAccountRepository.findByUserId(1L)).thenReturn(existingAccounts);

        assertThrows(RuntimeException.class, () -> accountService.addAccount(bankAccountRequestDTO));
    }

    @Test
    void getAccountById_Success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        BankAccountResponseDTO result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAccountById_NotFound() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void getAccountByIBAN_Success() {
        when(bankAccountRepository.findByRib("1234567890123456")).thenReturn(bankAccount);
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        BankAccountResponseDTO result = accountService.getAccountByIBAN("1234567890123456");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAccounts_Success() {
        when(bankAccountRepository.findAll()).thenReturn(Collections.singletonList(bankAccount));
        when(authRestClient.getUserById(1L)).thenReturn(userDTO);
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        List<BankAccountResponseDTO> result = accountService.getAccounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(authRestClient, times(1)).getUserById(1L);
    }

    @Test
    void getAccountsByUserId_Success() {
        when(bankAccountRepository.findByUserId(1L)).thenReturn(Collections.singletonList(bankAccount));
        when(authRestClient.getUserById(1L)).thenReturn(userDTO);
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        List<BankAccountResponseDTO> result = accountService.getAccountsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateAccount_Success() {
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setBalance(2000.0);
        updateDTO.setIsActive(false);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = accountService.updateAccount(1L, updateDTO);

        assertNotNull(result);
        assertEquals(2000.0, result.getBalance());
        assertEquals(false, result.getIsActive());
    }

    @Test
    void deleteAccount_Success() {
        doNothing().when(bankAccountRepository).deleteById(1L);

        accountService.deleteAccount(1L);

        verify(bankAccountRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateBalance_Add_Success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        accountService.updateBalance(1L, 500.0, "ADD");

        assertEquals(1500.0, bankAccount.getBalance());
    }

    @Test
    void updateBalance_Subtract_Success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        accountService.updateBalance(1L, 500.0, "SUBTRACT");

        assertEquals(500.0, bankAccount.getBalance());
    }

    @Test
    void updateBalance_Subtract_InsufficientBalance() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        assertThrows(RuntimeException.class, () -> accountService.updateBalance(1L, 2000.0, "SUBTRACT"));
    }

    @Test
    void getAccountDistribution_Success() {
        List<Object[]> distribution = new ArrayList<>();
        distribution.add(new Object[]{AccountType.CURRENT_ACCOUNT, 10L});
        distribution.add(new Object[]{AccountType.SAVING_ACCOUNT, 5L});

        when(bankAccountRepository.countTotalAccountsByType()).thenReturn(distribution);

        List<AccountDistributionDTO> result = accountService.getAccountDistribution();

        assertEquals(2, result.size());
        assertEquals(AccountType.CURRENT_ACCOUNT, result.get(0).getType());
        assertEquals(10L, result.get(0).getCount());
    }

    @Test
    void countUsers_Success() {
        when(bankAccountRepository.count()).thenReturn(42L);

        Long result = accountService.countUsers();

        assertNotNull(result);
        assertEquals(42L, result);
        verify(bankAccountRepository, times(1)).count();
    }

    @Test
    void enrichAccountWithUser_NullUserId() {
        bankAccount.setUserId(null);
        when(bankAccountRepository.findAll()).thenReturn(Collections.singletonList(bankAccount));
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        List<BankAccountResponseDTO> result = accountService.getAccounts();

        assertNotNull(result);
        verify(authRestClient, never()).getUserById(any());
    }

    @Test
    void enrichAccountWithUser_NullUserDTO() {
        when(bankAccountRepository.findAll()).thenReturn(Collections.singletonList(bankAccount));
        when(authRestClient.getUserById(1L)).thenReturn(null);
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        List<BankAccountResponseDTO> result = accountService.getAccounts();

        assertNotNull(result);
        assertNull(bankAccount.getUser());
    }

    @Test
    void updateBalance_InvalidOperation() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> accountService.updateBalance(1L, 100.0, "INVALID"));
    }

    @Test
    void updateBalance_AccountNotFound() {
        when(bankAccountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountService.updateBalance(999L, 100.0, "ADD"));
    }

    @Test
    void getTransactionsForAccount_Success() {
        String token = "Bearer token";
        when(authRestClient.validateToken(token)).thenReturn(true);
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(transactionRestClient.getTransactionsByAccount(1L, token)).thenReturn(Collections.emptyList());

        List<TransactionResponseDTO> result = accountService.getTransactionsForAccount(1L, token);

        assertNotNull(result);
        verify(transactionRestClient).getTransactionsByAccount(1L, token);
    }

    @Test
    void getTransactionsForAccount_Unauthorized() {
        String token = "Bearer token";
        when(authRestClient.validateToken(token)).thenReturn(false);

        assertThrows(com.lachguer.accountservice.exception.UnauthorizedException.class, 
            () -> accountService.getTransactionsForAccount(1L, token));
    }
    
    @Test
    void getTransactionsForAccount_MissingToken() {
        assertThrows(com.lachguer.accountservice.exception.UnauthorizedException.class, 
            () -> accountService.getTransactionsForAccount(1L, null));
    }

    @Test
    void createTransactionForAccount_Success() {
        String token = "Bearer token";
        TransactionRequestDTO request = new TransactionRequestDTO();
        TransactionResponseDTO response = new TransactionResponseDTO();
        
        when(authRestClient.validateToken(token)).thenReturn(true);
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(transactionRestClient.createTransaction(any(TransactionRequestDTO.class), eq(token))).thenReturn(response);

        TransactionResponseDTO result = accountService.createTransactionForAccount(1L, request, token);

        assertNotNull(result);
        verify(transactionRestClient).createTransaction(request, token);
    }

    @Test
    void updateAccount_PartialUpdate() {
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setIsContactless(true);
        updateDTO.setIsWithdrawal(true);
        updateDTO.setIsPaymentByCard(true);
        updateDTO.setIsOnlinePayment(true);
        updateDTO.setOverDraft(500.0);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = accountService.updateAccount(1L, updateDTO);

        assertEquals(true, result.getIsContactless());
        assertEquals(true, result.getIsWithdrawal());
        assertEquals(true, result.getIsPaymentByCard());
        assertEquals(true, result.getIsOnlinePayment());
        assertEquals(500.0, ((CurrentAccount) result).getOverDraft());
    }

    @Test
    void updateAccount_SavingAccountUpdate() {
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(2L);
        savingAccount.setInterestRate(3.0);
        
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setInterestRate(4.0);

        when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(savingAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savingAccount);

        BankAccount result = accountService.updateAccount(2L, updateDTO);

        assertEquals(4.0, ((SavingAccount) result).getInterestRate());
    }
    
    @Test
    void updateAccount_AllFields() {
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setBalance(5000.0);
        updateDTO.setIsActive(true);
        updateDTO.setIsContactless(false);
        updateDTO.setIsWithdrawal(false);
        updateDTO.setIsPaymentByCard(true);
        updateDTO.setIsOnlinePayment(true);
        updateDTO.setOverDraft(1200.0);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount result = accountService.updateAccount(1L, updateDTO);

        assertEquals(5000.0, result.getBalance());
        assertEquals(true, result.getIsActive());
        assertEquals(false, result.getIsContactless());
        assertEquals(false, result.getIsWithdrawal());
        assertEquals(true, result.getIsPaymentByCard());
        assertEquals(true, result.getIsOnlinePayment());
        assertEquals(1200.0, ((CurrentAccount) result).getOverDraft());
    }

    @Test
    void updateAccount_PartialNullFields() {
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setIsActive(null);
        updateDTO.setBalance(null);
        updateDTO.setOverDraft(null);
        updateDTO.setInterestRate(null);

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount result = accountService.updateAccount(1L, updateDTO);
        assertEquals(1000.0, result.getBalance());
    }

    @Test
    void updateAccount_SavingAccountPartialNull() {
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(2L);
        savingAccount.setInterestRate(3.0);
        
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setInterestRate(null);

        when(bankAccountRepository.findById(2L)).thenReturn(Optional.of(savingAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount result = accountService.updateAccount(2L, updateDTO);
        assertEquals(3.0, ((SavingAccount) result).getInterestRate());
    }
    
    @Test
    void getTransactionsForAccount_EmptyToken() {
        assertThrows(com.lachguer.accountservice.exception.UnauthorizedException.class, 
            () -> accountService.getTransactionsForAccount(1L, ""));
    }

    @Test
    void enrichAccountWithUser_AuthServiceFailure() {
        // Setup: Auth service throws exception
        when(bankAccountRepository.findAll()).thenReturn(Collections.singletonList(bankAccount));
        when(authRestClient.getUserById(1L)).thenThrow(new RuntimeException("Service down"));
        when(accountMapper.fromBankAccount(bankAccount)).thenReturn(bankAccountResponseDTO);

        // Execute
        List<BankAccountResponseDTO> result = accountService.getAccounts();

        // Verify: Should proceed without error, just missing user info
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
