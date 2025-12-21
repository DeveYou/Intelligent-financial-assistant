package com.khaoula.transactionsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaoula.transactionsservice.dto.TransactionResponseDTO;
import com.khaoula.transactionsservice.dto.TransferRequestDTO;
import com.khaoula.transactionsservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(TransactionUserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simplicity in unit tests
class TransactionUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void transfer_Success() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setBankAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setRecipientIban("FR7612345678901234567890123");
        request.setReason("Test Transfer");

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(new BigDecimal("100.00"));

        when(transactionService.createTransfer(any(TransferRequestDTO.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/user/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyTransactions_Success() throws Exception {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(1L);
        response.setAmount(new BigDecimal("100.00"));

        when(transactionService.getUserTransactions(anyLong())).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/user/transactions/my-transactions")
                        .header("X-Auth-User-Id", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyTransactions_MissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/user/transactions/my-transactions")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyTransactions_InvalidUserIdHeader() throws Exception {
        mockMvc.perform(get("/user/transactions/my-transactions")
                        .header("X-Auth-User-Id", "invalid")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
