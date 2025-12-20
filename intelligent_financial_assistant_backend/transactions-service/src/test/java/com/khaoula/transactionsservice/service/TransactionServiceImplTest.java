package com.khaoula.transactionsservice.service;

import com.khaoula.transactionsservice.client.AccountClient;
import com.khaoula.transactionsservice.client.RecipientClient;
import com.khaoula.transactionsservice.client.UserClient;
import com.khaoula.transactionsservice.domain.Transaction;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.DailyTransactionStats;
import com.khaoula.transactionsservice.dto.RecipientRequest;
import com.khaoula.transactionsservice.dto.TransactionFilterDTO;
import com.khaoula.transactionsservice.dto.TransactionRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.exception.InsufficientBalanceException;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.exception.ResourceNotFoundException;
import com.khaoula.transactionsservice.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private RecipientClient recipientClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDTO transactionRequestDTO;
    private TransferRequestDTO transferRequestDTO;
    private Transaction transaction;
    private AccountClient.AccountResponse accountResponse;
    private RecipientClient.RecipientResponse recipientResponse;

    @BeforeEach
    void setUp() {
        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setBankAccountId(1L);
        transactionRequestDTO.setAmount(new BigDecimal("100.00"));
        transactionRequestDTO.setReason("Test Transaction");

        transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBankAccountId(1L);
        transferRequestDTO.setAmount(new BigDecimal("100.00"));
        transferRequestDTO.setRecipientIban("FR7612345678901234567890123");
        transferRequestDTO.setReason("Test Transfer");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUserId(1L);
        transaction.setBankAccountId(1L);
        transaction.setReference("TXN-12345678");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(OffsetDateTime.now());

        accountResponse = new AccountClient.AccountResponse();
        accountResponse.setId(1L);
        accountResponse.setUserId(1L);
        accountResponse.setIsActive(true);
        accountResponse.setBalance(1000.0);

        recipientResponse = new RecipientClient.RecipientResponse();
        recipientResponse.setId(1L);
        recipientResponse.setFullName("John Doe");
        recipientResponse.setIban("FR7612345678901234567890123");
    }

    @Test
    void createDeposit_Success() {
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doNothing().when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        TransactionResponseDTO result = transactionService.createDeposit(transactionRequestDTO, "Bearer token");

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(accountClient, times(1)).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());
    }

    @Test
    void createDeposit_AccountInactive() {
        accountResponse.setIsActive(false);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);

        assertThrows(InvalidTransactionException.class, () -> transactionService.createDeposit(transactionRequestDTO, "Bearer token"));
    }

    @Test
    void createWithdrawal_Success() {
        transaction.setType(TransactionType.WITHDRAWAL);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doNothing().when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        TransactionResponseDTO result = transactionService.createWithdrawal(transactionRequestDTO, "Bearer token");

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
    }

    @Test
    void createWithdrawal_InsufficientBalance() {
        accountResponse.setBalance(50.0);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);

        assertThrows(InsufficientBalanceException.class, () -> transactionService.createWithdrawal(transactionRequestDTO, "Bearer token"));
    }

    @Test
    void createTransfer_Success() {
        transaction.setType(TransactionType.TRANSFER);
        RecipientClient.ApiResponse<RecipientClient.RecipientResponse> apiResponse = new RecipientClient.ApiResponse<>();
        apiResponse.setData(recipientResponse);

        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(recipientClient.getRecipientByIban(anyString(), anyString())).thenReturn(apiResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doNothing().when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());
        when(accountClient.getAccountByIban(anyString(), anyString())).thenReturn(accountResponse);

        TransactionResponseDTO result = transactionService.createTransfer(transferRequestDTO, "Bearer token");

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
    }

    @Test
    void getUserTransactions_Success() {
        when(transactionRepository.findByUserId(1L)).thenReturn(Collections.singletonList(transaction));

        List<TransactionResponseDTO> result = transactionService.getUserTransactions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionResponseDTO result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void cancelTransaction_Success() {
        transaction.setStatus(TransactionStatus.PENDING);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseDTO result = transactionService.cancelTransaction(1L, 1L);

        assertNotNull(result);
        assertEquals(TransactionStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelTransaction_NotPending() {
        transaction.setStatus(TransactionStatus.COMPLETED);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(InvalidTransactionException.class, () -> transactionService.cancelTransaction(1L, 1L));
    }

    @Test
    void cancelTransaction_Unauthorized() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(InvalidTransactionException.class, () -> transactionService.cancelTransaction(1L, 2L));
    }

    @Test
    void createTransfer_InsufficientBalance() {
        accountResponse.setBalance(50.0);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);

        assertThrows(InsufficientBalanceException.class, () -> transactionService.createTransfer(transferRequestDTO, "Bearer token"));
    }

    @Test
    void createTransfer_InactiveAccount() {
        accountResponse.setIsActive(false);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);

        assertThrows(InvalidTransactionException.class, () -> transactionService.createTransfer(transferRequestDTO, "Bearer token"));
    }

    @Test
    void createTransfer_RecipientNotFoundAndAutoCreateFails() {
        RecipientClient.ApiResponse<RecipientClient.RecipientResponse> apiResponse = new RecipientClient.ApiResponse<>();
        
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(recipientClient.getRecipientByIban(anyString(), anyString())).thenThrow(new RuntimeException("Recipient not found"));
        when(accountClient.getAccountByIban(anyString(), anyString())).thenThrow(new RuntimeException("Account not found"));

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransfer(transferRequestDTO, "Bearer token"));
    }

    @Test
    void createTransfer_NullRecipientIban() {
        transferRequestDTO.setRecipientIban(null);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);

        assertThrows(InvalidTransactionException.class, () -> transactionService.createTransfer(transferRequestDTO, "Bearer token"));
    }

    @Test
    void createTransfer_BalanceUpdateFails() {
        transaction.setType(TransactionType.TRANSFER);
        RecipientClient.ApiResponse<RecipientClient.RecipientResponse> apiResponse = new RecipientClient.ApiResponse<>();
        apiResponse.setData(recipientResponse);

        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(recipientClient.getRecipientByIban(anyString(), anyString())).thenReturn(apiResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doThrow(new RuntimeException("Balance update failed")).when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        assertThrows(InvalidTransactionException.class, () -> transactionService.createTransfer(transferRequestDTO, "Bearer token"));
    }

    @Test
    void createDeposit_BalanceUpdateFails() {
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doThrow(new RuntimeException("Balance update failed")).when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        assertThrows(InvalidTransactionException.class, () -> transactionService.createDeposit(transactionRequestDTO, "Bearer token"));
    }

    @Test
    void createWithdrawal_BalanceUpdateFails() {
        transaction.setType(TransactionType.WITHDRAWAL);
        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doThrow(new RuntimeException("Balance update failed")).when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        assertThrows(InvalidTransactionException.class, () -> transactionService.createWithdrawal(transactionRequestDTO, "Bearer token"));
    }

    @Test
    void getAccountTransactions_Success() {
        when(transactionRepository.findByBankAccountId(1L)).thenReturn(Collections.singletonList(transaction));

        List<TransactionResponseDTO> result = transactionService.getAccountTransactions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTransactionByReference_Success() {
        when(transactionRepository.findByReference("TXN-12345678")).thenReturn(Optional.of(transaction));

        TransactionResponseDTO result = transactionService.getTransactionByReference("TXN-12345678");

        assertNotNull(result);
        assertEquals("TXN-12345678", result.getReference());
    }

    @Test
    void getTransactionByReference_NotFound() {
        when(transactionRepository.findByReference("TXN-INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionByReference("TXN-INVALID"));
    }

    @Test
    void getAllTransactions_WithFilters() {
        TransactionFilterDTO filter = new TransactionFilterDTO();
        filter.setPage(0);
        filter.setSize(10);
        filter.setSortBy("date");
        filter.setSortDirection("DESC");
        filter.setUserId(1L);
        filter.setType(TransactionType.DEPOSIT);
        filter.setStatus(TransactionStatus.COMPLETED);

        Page<Transaction> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findByFilters(anyLong(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<TransactionResponseDTO> result = transactionService.getAllTransactions(filter);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }



    @Test
    void getTransactionStats_Success() {
        when(transactionRepository.count()).thenReturn(100L);
        when(transactionRepository.countByStatus(TransactionStatus.PENDING)).thenReturn(10L);
        when(transactionRepository.countByStatus(TransactionStatus.COMPLETED)).thenReturn(80L);
        when(transactionRepository.countByStatus(TransactionStatus.FAILED)).thenReturn(10L);
        when(transactionRepository.sumAmountByStatus(TransactionStatus.COMPLETED)).thenReturn(50000.0);
        when(transactionRepository.countByDateBetween(any(), any())).thenReturn(15L);

        TransactionService.TransactionStatsDTO stats = transactionService.getTransactionStats();

        assertNotNull(stats);
        assertEquals(100L, stats.getTotalTransactions());
        assertEquals(10L, stats.getPendingTransactions());
        assertEquals(80L, stats.getCompletedTransactions());
        assertEquals(10L, stats.getFailedTransactions());
        assertEquals(50000.0, stats.getTotalVolume());
        assertEquals(15L, stats.getTodayTransactions());
    }

    @Test
    void getTransactionStats_NullTotalVolume() {
        when(transactionRepository.count()).thenReturn(10L);
        when(transactionRepository.countByStatus(TransactionStatus.PENDING)).thenReturn(5L);
        when(transactionRepository.countByStatus(TransactionStatus.COMPLETED)).thenReturn(5L);
        when(transactionRepository.countByStatus(TransactionStatus.FAILED)).thenReturn(0L);
        when(transactionRepository.sumAmountByStatus(TransactionStatus.COMPLETED)).thenReturn(null);
        when(transactionRepository.countByDateBetween(any(), any())).thenReturn(2L);

        TransactionService.TransactionStatsDTO stats = transactionService.getTransactionStats();

        assertNotNull(stats);
        assertEquals(0.0, stats.getTotalVolume());
    }

    @Test
    void getDailyStats_Success() {
        DailyTransactionStats stats = new DailyTransactionStats();
        when(transactionRepository.findDailyStats(any())).thenReturn(Collections.singletonList(stats));

        List<DailyTransactionStats> result = transactionService.getDailyStats();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findDailyStats(any());
    }

    @Test
    void createTransfer_AutoCreateRecipient() {
        transaction.setType(TransactionType.TRANSFER);
        RecipientClient.ApiResponse<RecipientClient.RecipientResponse> createResponse = new RecipientClient.ApiResponse<>();
        createResponse.setData(recipientResponse);
        
        UserClient.UserDetails userDetails = new UserClient.UserDetails();
        userDetails.setFirstName("John");
        userDetails.setLastName("Doe");

        when(accountClient.getAccountById(anyLong(), anyString())).thenReturn(accountResponse);
        when(recipientClient.getRecipientByIban(anyString(), anyString())).thenThrow(new RuntimeException("Not found"));
        when(accountClient.getAccountByIban(anyString(), anyString())).thenReturn(accountResponse);
        when(userClient.getUserById(anyLong(), anyString())).thenReturn(userDetails);
        when(recipientClient.addRecipient(any(RecipientRequest.class), anyString())).thenReturn(createResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        doNothing().when(accountClient).updateBalance(anyLong(), any(AccountClient.BalanceUpdateRequest.class), anyString());

        TransactionResponseDTO result = transactionService.createTransfer(transferRequestDTO, "Bearer token");

        assertNotNull(result);
        verify(recipientClient, times(1)).addRecipient(any(RecipientRequest.class), anyString());
    }
}
