package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.client.AuthClient;
import com.khaoula.transactionsservice.client.BankAccountClient;
import com.khaoula.transactionsservice.client.RecipientClient;
import com.khaoula.transactionsservice.dto.BankAccountDTO;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.UserDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.dto.RecipientDTO;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private BankAccountClient bankAccountClient;

    @Mock
    private RecipientClient recipientClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private DepositRequestDTO depositRequest;
    private WithdrawalRequestDTO withdrawalRequest;
    private TransferRequestDTO transferRequest;
    private UserDTO user;
    private BankAccountDTO bankAccount;
    private BankAccountDTO targetBankAccount;

    @BeforeEach
    void setUp() {
        depositRequest = new DepositRequestDTO();
        depositRequest.setBankAccountId("ACC-1");
        depositRequest.setAmount(BigDecimal.valueOf(100));
        depositRequest.setReason("Salary");
        depositRequest.setUserId(1L);

        withdrawalRequest = new WithdrawalRequestDTO();
        withdrawalRequest.setBankAccountId("ACC-1");
        withdrawalRequest.setAmount(BigDecimal.valueOf(50));
        withdrawalRequest.setReason("ATM");
        withdrawalRequest.setUserId(1L);

        transferRequest = new TransferRequestDTO();
        transferRequest.setSourceAccountId("ACC-1");
        transferRequest.setTargetAccountId("ACC-2");
        transferRequest.setAmount(BigDecimal.valueOf(75));
        transferRequest.setReason("Rent");
        transferRequest.setUserId(1L);

        user = new UserDTO();
        user.setId(1L);

        bankAccount = new BankAccountDTO();
        bankAccount.setRib("ACC-1");
        bankAccount.setUserId(1L);
        bankAccount.setActive(true);
        bankAccount.setBalance(BigDecimal.valueOf(200));

        targetBankAccount = new BankAccountDTO();
        targetBankAccount.setRib("ACC-2");
        targetBankAccount.setUserId(2L);
        targetBankAccount.setActive(true);
    }

    // =================================================================
    // Deposit Tests
    // =================================================================

    @Test
    void deposit_shouldSucceed_whenDataIsValid() {
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponseDTO response = transactionService.deposit(1L, depositRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        Transaction finalTx = txCaptor.getAllValues().get(1);

        assertEquals(TransactionStatus.COMPLETED, finalTx.getStatus());
        assertEquals(1L, finalTx.getUserId());
        verify(bankAccountClient).deposit(depositRequest);
    }

    @Test
    void deposit_shouldSetStatusToFailed_whenBankAccountClientFails() {
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        doThrow(new RuntimeException("Service unavailable")).when(bankAccountClient).deposit(any());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        transactionService.deposit(1L, depositRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        Transaction finalTx = txCaptor.getAllValues().get(1);

        assertEquals(TransactionStatus.FAILED, finalTx.getStatus());
    }

    @Test
    void deposit_shouldThrowException_whenAccountDoesNotBelongToUser() {
        bankAccount.setUserId(99L); // Different user
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.deposit(1L, depositRequest));

        assertTrue(ex.getMessage().contains("Bank account not found, inactive, or does not belong to the user"));
        verify(transactionRepository, never()).save(any());
    }

    // =================================================================
    // Withdrawal Tests
    // =================================================================

    @Test
    void withdraw_shouldSucceed_whenDataIsValid() {
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        transactionService.withdraw(1L, withdrawalRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        Transaction finalTx = txCaptor.getAllValues().get(1);

        assertEquals(TransactionStatus.COMPLETED, finalTx.getStatus());
        verify(bankAccountClient).withdraw(withdrawalRequest);
    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientFunds() {
        bankAccount.setBalance(BigDecimal.valueOf(20)); // Not enough for 50
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.withdraw(1L, withdrawalRequest));

        assertTrue(ex.getMessage().contains("Insufficient funds"));
        verify(transactionRepository, never()).save(any());
    }

    // =================================================================
    // Transfer Tests
    // =================================================================

    @Test
    void transfer_shouldSucceed_whenDataIsValid() {
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(bankAccountClient.getAccountByRib("ACC-2")).thenReturn(targetBankAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        transactionService.transfer(1L, transferRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        Transaction finalTx = txCaptor.getAllValues().get(1);

        assertEquals(TransactionStatus.COMPLETED, finalTx.getStatus());
        verify(bankAccountClient).transfer(transferRequest);
    }

    @Test
    void transfer_shouldThrowException_whenSourceAccountDoesNotBelongToUser() {
        bankAccount.setUserId(99L); // Different user
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(bankAccountClient.getAccountByRib("ACC-2")).thenReturn(targetBankAccount);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.transfer(1L, transferRequest));

        assertTrue(ex.getMessage().contains("Source account does not belong to the authenticated user"));
    }

    @Test
    void transfer_shouldThrowException_whenTargetAccountIsInactive() {
        targetBankAccount.setActive(false);
        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(bankAccountClient.getAccountByRib("ACC-2")).thenReturn(targetBankAccount);

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.transfer(1L, transferRequest));

        assertTrue(ex.getMessage().contains("Target account not found or inactive"));
    }

    @Test
    void transfer_shouldResolveRecipientByIban_whenTargetNotFoundAsAccount() {
        // CAS: target account not found in account-service, but exists as recipient with IBAN
        transferRequest.setTargetAccountId("IBAN-XYZ");

        when(authClient.getUserById(1L)).thenReturn(user);
        when(bankAccountClient.getAccountByRib("ACC-1")).thenReturn(bankAccount);
        when(bankAccountClient.getAccountByRib("IBAN-XYZ")).thenReturn(null);

        RecipientDTO recipientDTO = new RecipientDTO();
        recipientDTO.setIban("IBAN-XYZ");
        recipientDTO.setFullName("John Doe");
        recipientDTO.setId(123L);

        when(recipientClient.getByIban("IBAN-XYZ")).thenReturn(recipientDTO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        transactionService.transfer(1L, transferRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        Transaction finalTx = txCaptor.getAllValues().get(1);

        assertEquals(TransactionStatus.COMPLETED, finalTx.getStatus());
        assertEquals(123L, finalTx.getRecipientId());
        verify(bankAccountClient).transfer(transferRequest);
    }

    // =================================================================
    // General Tests
    // =================================================================

    @Test
    void getByReference_shouldReturnTransaction() {
        Transaction tx = buildTransaction(1L, TransactionType.DEPOSIT, "ACC-1", null, BigDecimal.TEN, "Test");
        when(transactionRepository.findByReference("REF-1")).thenReturn(Optional.of(tx));

        TransactionResponseDTO response = transactionService.getByReference("REF-1");

        assertEquals(tx.getReference(), response.getReference());
        assertEquals(tx.getId(), response.getId());
    }

    @Test
    void getByReference_shouldThrowException_whenNotFound() {
        when(transactionRepository.findByReference("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(InvalidTransactionException.class, () -> transactionService.getByReference("UNKNOWN"));
    }

    private Transaction buildTransaction(Long id,
                                         TransactionType type,
                                         String bankAccountId,
                                         Long recipientId,
                                         BigDecimal amount,
                                         String reason) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setUserId(1L);
        tx.setType(type);
        tx.setBankAccountId(bankAccountId);
        tx.setRecipientId(recipientId);
        tx.setAmount(amount);
        tx.setReason(reason);
        tx.setReference("REF-" + id);
        tx.setDate(OffsetDateTime.now());
        tx.setStatus(TransactionStatus.COMPLETED);
        return tx;
    }
}
