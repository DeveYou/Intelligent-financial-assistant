package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
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

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private DepositRequestDTO depositRequest;
    private WithdrawalRequestDTO withdrawalRequest;
    private TransferRequestDTO transferRequest;

    @BeforeEach
    void setUp() {
        depositRequest = new DepositRequestDTO();
        depositRequest.setBankAccountId("ACC-1");
        depositRequest.setAmount(BigDecimal.valueOf(100));
        depositRequest.setReason("Salary");

        withdrawalRequest = new WithdrawalRequestDTO();
        withdrawalRequest.setBankAccountId("ACC-1");
        withdrawalRequest.setAmount(BigDecimal.valueOf(50));
        withdrawalRequest.setReason("ATM");

        transferRequest = new TransferRequestDTO();
        transferRequest.setSourceAccountId("ACC-1");
        transferRequest.setTargetAccountId("ACC-2");
        transferRequest.setAmount(BigDecimal.valueOf(75));
        transferRequest.setReason("Rent");
    }

    @Test
    void deposit_shouldPersistTransactionAndReturnResponse() {
        when(transactionRepository.findByReference(anyString())).thenReturn(Optional.empty());

        Transaction saved = buildTransaction(1L, TransactionType.DEPOSIT, "ACC-1", null, BigDecimal.valueOf(100), "Salary");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO response = transactionService.deposit(depositRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction captured = txCaptor.getValue();

        assertEquals(TransactionType.DEPOSIT, captured.getType());
        assertEquals(depositRequest.getBankAccountId(), captured.getBankAccountId());
        assertEquals(depositRequest.getAmount(), captured.getAmount());
        assertEquals(depositRequest.getReason(), captured.getReason());
        assertNotNull(captured.getReference());
        assertNotNull(captured.getDate());

        assertEquals(saved.getId(), response.getId());
        assertEquals(saved.getBankAccountId(), response.getBankAccountId());
        assertEquals(saved.getAmount(), response.getAmount());
        assertEquals(saved.getType(), response.getType());
        assertEquals(saved.getReference(), response.getReference());
    }

    @Test
    void deposit_shouldThrowInvalidTransactionException_whenAmountIsNullOrNonPositive() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setBankAccountId("ACC-1");

        request.setAmount(null);
        assertThrows(InvalidTransactionException.class, () -> transactionService.deposit(request));

        request.setAmount(BigDecimal.ZERO);
        assertThrows(InvalidTransactionException.class, () -> transactionService.deposit(request));

        request.setAmount(BigDecimal.valueOf(-10));
        assertThrows(InvalidTransactionException.class, () -> transactionService.deposit(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void withdraw_shouldPersistTransactionAndReturnResponse() {
        when(transactionRepository.findByReference(anyString())).thenReturn(Optional.empty());

        Transaction saved = buildTransaction(2L, TransactionType.WITHDRAWAL, "ACC-1", null, BigDecimal.valueOf(50), "ATM");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO response = transactionService.withdraw(withdrawalRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction captured = txCaptor.getValue();

        assertEquals(TransactionType.WITHDRAWAL, captured.getType());
        assertEquals(withdrawalRequest.getBankAccountId(), captured.getBankAccountId());
        assertEquals(withdrawalRequest.getAmount(), captured.getAmount());
        assertEquals(withdrawalRequest.getReason(), captured.getReason());
        assertNotNull(captured.getReference());
        assertNotNull(captured.getDate());

        assertEquals(saved.getId(), response.getId());
        assertEquals(saved.getType(), response.getType());
    }

    @Test
    void withdraw_shouldThrowInvalidTransactionException_whenAmountIsInvalid() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setBankAccountId("ACC-1");

        request.setAmount(null);
        assertThrows(InvalidTransactionException.class, () -> transactionService.withdraw(request));

        request.setAmount(BigDecimal.ZERO);
        assertThrows(InvalidTransactionException.class, () -> transactionService.withdraw(request));

        request.setAmount(BigDecimal.valueOf(-1));
        assertThrows(InvalidTransactionException.class, () -> transactionService.withdraw(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldPersistTransactionAndReturnResponse() {
        when(transactionRepository.findByReference(anyString())).thenReturn(Optional.empty());

        Transaction saved = buildTransaction(3L, TransactionType.TRANSFER, "ACC-1", "ACC-2", BigDecimal.valueOf(75), "Rent");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO response = transactionService.transfer(transferRequest);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction captured = txCaptor.getValue();

        assertEquals(TransactionType.TRANSFER, captured.getType());
        assertEquals(transferRequest.getSourceAccountId(), captured.getBankAccountId());
        assertEquals(transferRequest.getTargetAccountId(), captured.getReceiver());
        assertEquals(transferRequest.getAmount(), captured.getAmount());
        assertEquals(transferRequest.getReason(), captured.getReason());
        assertNotNull(captured.getReference());
        assertNotNull(captured.getDate());

        assertEquals(saved.getId(), response.getId());
        assertEquals(saved.getReceiver(), response.getReceiver());
        assertEquals(saved.getType(), response.getType());
    }

    @Test
    void transfer_shouldThrowInvalidTransactionException_whenAmountIsInvalid() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId("ACC-1");
        request.setTargetAccountId("ACC-2");

        request.setAmount(null);
        assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(request));

        request.setAmount(BigDecimal.ZERO);
        assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(request));

        request.setAmount(BigDecimal.valueOf(-5));
        assertThrows(InvalidTransactionException.class, () -> transactionService.transfer(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowInvalidTransactionException_whenSourceEqualsTarget() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId("ACC-1");
        request.setTargetAccountId("ACC-1");
        request.setAmount(BigDecimal.valueOf(10));

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.transfer(request));

        assertTrue(ex.getMessage().contains("Source and target accounts must be different"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getHistoryByAccount_shouldReturnMappedList() {
        Transaction t1 = buildTransaction(1L, TransactionType.DEPOSIT, "ACC-1", null, BigDecimal.TEN, "A");
        Transaction t2 = buildTransaction(2L, TransactionType.WITHDRAWAL, "ACC-1", null, BigDecimal.ONE, "B");

        when(transactionRepository.findByBankAccountIdOrderByDateDesc("ACC-1"))
                .thenReturn(List.of(t1, t2));

        List<TransactionResponseDTO> history = transactionService.getHistoryByAccount("ACC-1");

        assertEquals(2, history.size());
        assertEquals(t1.getId(), history.get(0).getId());
        assertEquals(t2.getId(), history.get(1).getId());
    }

    @Test
    void getHistoryByAccount_shouldReturnEmptyList_whenNoTransactions() {
        when(transactionRepository.findByBankAccountIdOrderByDateDesc("ACC-1"))
                .thenReturn(List.of());

        List<TransactionResponseDTO> history = transactionService.getHistoryByAccount("ACC-1");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getByReference_shouldReturnMappedTransaction() {
        Transaction t = buildTransaction(10L, TransactionType.DEPOSIT, "ACC-1", null, BigDecimal.TEN, "GIFT");
        when(transactionRepository.findByReference("REF-1")).thenReturn(Optional.of(t));

        TransactionResponseDTO response = transactionService.getByReference("REF-1");

        assertEquals(t.getId(), response.getId());
        assertEquals(t.getReference(), response.getReference());
        assertEquals(t.getType(), response.getType());
        assertEquals(t.getAmount(), response.getAmount());
    }

    @Test
    void getByReference_shouldThrowInvalidTransactionException_whenNotFound() {
        when(transactionRepository.findByReference("UNKNOWN")).thenReturn(Optional.empty());

        InvalidTransactionException ex = assertThrows(InvalidTransactionException.class,
                () -> transactionService.getByReference("UNKNOWN"));

        assertTrue(ex.getMessage().contains("Transaction not found with reference"));
    }

    @Test
    void generateUniqueReference_shouldRetryWhenReferenceAlreadyExists() {
        // First call: reference already exists, second call: free
        when(transactionRepository.findByReference(anyString()))
                .thenReturn(Optional.of(new Transaction()))
                .thenReturn(Optional.empty());

        Transaction saved = buildTransaction(5L, TransactionType.DEPOSIT, "ACC-1", null, BigDecimal.TEN, "Test");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO response = transactionService.deposit(depositRequest);

        assertNotNull(response.getReference());
        verify(transactionRepository, atLeast(2)).findByReference(anyString());
    }

    private Transaction buildTransaction(Long id,
                                         TransactionType type,
                                         String bankAccountId,
                                         String receiver,
                                         BigDecimal amount,
                                         String reason) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setType(type);
        tx.setBankAccountId(bankAccountId);
        tx.setReceiver(receiver);
        tx.setAmount(amount);
        tx.setReason(reason);
        tx.setReference("REF-" + id);
        tx.setDate(OffsetDateTime.now());
        return tx;
    }
}
