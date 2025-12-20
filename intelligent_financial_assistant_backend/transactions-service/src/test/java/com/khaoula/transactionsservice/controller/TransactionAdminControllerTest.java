package com.khaoula.transactionsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import com.khaoula.transactionsservice.dto.TransactionFilterDTO;
import com.khaoula.transactionsservice.dto.TransactionRequestDTO;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.dto.DailyTransactionStats;
import com.khaoula.transactionsservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionAdminController.class)
@AutoConfigureMockMvc
class TransactionAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTransactions_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        Page<TransactionResponseDTO> page = new PageImpl<>(Collections.singletonList(response));

        when(transactionService.getAllTransactions(any(TransactionFilterDTO.class))).thenReturn(page);

        mockMvc.perform(get("/admin/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTransactionById_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);

        when(transactionService.getTransactionById(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDeposit_Success() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setBankAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setReason("Test Deposit");

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setType(TransactionType.DEPOSIT);
        response.setStatus(TransactionStatus.COMPLETED);

        when(transactionService.createDeposit(any(TransactionRequestDTO.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/admin/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWithdrawal_Success() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setBankAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setReason("Test Withdrawal");

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setType(TransactionType.WITHDRAWAL);
        response.setStatus(TransactionStatus.COMPLETED);

        when(transactionService.createWithdrawal(any(TransactionRequestDTO.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/admin/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransfer_Success() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setBankAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setRecipientIban("FR7612345678901234567890123");
        request.setReason("Test Transfer");

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setType(TransactionType.TRANSFER);
        response.setStatus(TransactionStatus.COMPLETED);

        when(transactionService.createTransfer(any(TransferRequestDTO.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/admin/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStats_Success() throws Exception {
        TransactionService.TransactionStatsDTO stats = new TransactionService.TransactionStatsDTO(10L, 2L, 8L, 0L, 1000.0, 5L);

        when(transactionService.getTransactionStats()).thenReturn(stats);

        mockMvc.perform(get("/admin/transactions/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactions").value(10L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByReference_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setReference("TXN-12345678");

        when(transactionService.getTransactionByReference("TXN-12345678")).thenReturn(response);

        mockMvc.perform(get("/admin/transactions/reference/TXN-12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("TXN-12345678"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserTransactions_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setUserId(1L);

        when(transactionService.getUserTransactions(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/admin/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAccountTransactions_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setBankAccountId(1L);

        when(transactionService.getAccountTransactions(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/admin/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bankAccountId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDailyStats_Success() throws Exception {
        DailyTransactionStats stats = new DailyTransactionStats();

        when(transactionService.getDailyStats()).thenReturn(Collections.singletonList(stats));

        mockMvc.perform(get("/admin/transactions/stats/daily"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelTransaction_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setStatus(TransactionStatus.CANCELLED);

        when(transactionService.cancelTransaction(1L, 1L)).thenReturn(response);

        mockMvc.perform(post("/admin/transactions/1/cancel")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getAllTransactions_Unauthorized() throws Exception {
        mockMvc.perform(get("/admin/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }
}
