package com.khaoula.transactionsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaoula.transactionsservice.dto.DepositRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.WithdrawalRequestDTO;
import com.khaoula.transactionsservice.exception.InvalidTransactionException;
import com.khaoula.transactionsservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void deposit_shouldReturnCreatedTransaction() throws Exception {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setBankAccountId("ACC-1");
        request.setAmount(BigDecimal.valueOf(100));
        request.setReason("Salary");

        TransactionResponseDTO response = buildResponse(1L, "ACC-1", null, BigDecimal.valueOf(100), "Salary", "DEPOSIT");
        when(transactionService.deposit(any(DepositRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.bankAccountId", is("ACC-1")))
                .andExpect(jsonPath("$.type", is("DEPOSIT")))
                .andExpect(jsonPath("$.amount", is(100)));
    }

    @Test
    void deposit_shouldReturnBadRequest_whenAmountIsInvalid() throws Exception {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setBankAccountId("ACC-1");
        request.setAmount(BigDecimal.ZERO); // invalide pour la validation

        // Ne pas mocker le service avec une InvalidTransactionException ici :
        // on veut tester la validation automatique MethodArgumentNotValidException

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.amount", containsString("must be greater than 0")));
    }

    @Test
    void withdrawal_shouldReturnCreatedTransaction() throws Exception {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setBankAccountId("ACC-1");
        request.setAmount(BigDecimal.valueOf(50));
        request.setReason("ATM");

        TransactionResponseDTO response = buildResponse(2L, "ACC-1", null, BigDecimal.valueOf(50), "ATM", "WITHDRAWAL");
        when(transactionService.withdraw(any(WithdrawalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.type", is("WITHDRAWAL")));
    }

    @Test
    void transfer_shouldReturnCreatedTransaction() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId("ACC-1");
        request.setTargetAccountId("ACC-2");
        request.setAmount(BigDecimal.valueOf(75));
        request.setReason("Rent");

        TransactionResponseDTO response = buildResponse(3L, "ACC-1", "ACC-2", BigDecimal.valueOf(75), "Rent", "TRANSFER");
        when(transactionService.transfer(any(TransferRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.receiver", is("ACC-2")))
                .andExpect(jsonPath("$.type", is("TRANSFER")));
    }

    @Test
    void getHistoryByAccount_shouldReturnList() throws Exception {
        TransactionResponseDTO t1 = buildResponse(1L, "ACC-1", null, BigDecimal.TEN, "A", "DEPOSIT");
        TransactionResponseDTO t2 = buildResponse(2L, "ACC-1", null, BigDecimal.ONE, "B", "WITHDRAWAL");
        when(transactionService.getHistoryByAccount("ACC-1")).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/transactions/by-account/ACC-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getHistoryByAccount_shouldReturnEmptyList_whenNoTransactions() throws Exception {
        when(transactionService.getHistoryByAccount("ACC-1")).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/by-account/ACC-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getByReference_shouldReturnTransaction() throws Exception {
        TransactionResponseDTO response = buildResponse(10L, "ACC-1", null, BigDecimal.TEN, "Gift", "DEPOSIT");
        when(transactionService.getByReference("REF-1")).thenReturn(response);

        mockMvc.perform(get("/api/transactions/by-reference/REF-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.reference", is(response.getReference())));
    }

    @Test
    void getByReference_shouldReturnBadRequest_whenServiceThrowsInvalidTransactionException() throws Exception {
        when(transactionService.getByReference("UNKNOWN"))
                .thenThrow(new InvalidTransactionException("Transaction not found with reference"));

        mockMvc.perform(get("/api/transactions/by-reference/UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Transaction not found with reference")));
    }

    private TransactionResponseDTO buildResponse(Long id,
                                                 String bankAccountId,
                                                 String receiver,
                                                 BigDecimal amount,
                                                 String reason,
                                                 String type) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(id);
        dto.setBankAccountId(bankAccountId);
        dto.setReceiver(receiver);
        dto.setAmount(amount);
        dto.setReason(reason);
        dto.setType(Enum.valueOf(com.khaoula.transactionsservice.domain.TransactionType.class, type));
        dto.setReference("REF-" + id);
        dto.setDate(OffsetDateTime.now());
        return dto;
    }
}
