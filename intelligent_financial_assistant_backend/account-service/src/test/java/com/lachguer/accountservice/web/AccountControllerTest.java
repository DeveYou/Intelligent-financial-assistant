package com.lachguer.accountservice.web;

import com.aitsaid.commonsecurity.security.GatewayAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lachguer.accountservice.dto.AccountDistributionDTO;
import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.dto.BankAccountUpdateDTO;
import com.lachguer.accountservice.dto.TransactionRequestDTO;
import com.lachguer.accountservice.dto.TransactionResponseDTO;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GatewayAuthenticationFilter.class)
        })
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private com.lachguer.accountservice.repository.BankAccountRepository bankAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getAccounts_shouldReturnListOfAccounts() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        when(accountService.getAccounts()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser
    void getAccountById_shouldReturnAccount() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        when(accountService.getAccountById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAccountById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(accountService.getAccountById(1L)).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getAccountsByUserId_shouldReturnUserAccounts() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        when(accountService.getAccountsByUserId(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/accounts/user/1")
                        .header("X-Test-Header", "Value1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void addAccount_shouldCreateAccount() throws Exception {
        BankAccountRequestDTO requestDTO = new BankAccountRequestDTO();
        requestDTO.setType(AccountType.CURRENT_ACCOUNT);
        requestDTO.setUserId(1L);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(AccountType.CURRENT_ACCOUNT);
        responseDTO.setBalance(1000.0);

        when(accountService.addAccount(any(BankAccountRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void updateAccount_shouldUpdateAccount() throws Exception {
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setBalance(1500.0);
        updateDTO.setIsActive(true);
        updateDTO.setOverDraft(200.0);

        CurrentAccount updatedAccount = new CurrentAccount();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(1500.0);
        updatedAccount.setOverDraft(200.0);
        updatedAccount.setIsActive(true);
        updatedAccount.setAccountType(AccountType.CURRENT_ACCOUNT);

        when(accountService.updateAccount(anyLong(), any(BankAccountUpdateDTO.class)))
                .thenReturn(updatedAccount);

        mockMvc.perform(patch("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0))
                .andExpect(jsonPath("$.overDraft").value(200.0));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deleteAccount_shouldDeleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN", username = "adminUser")
    void countUsers_shouldReturnUserCount() throws Exception {
        when(accountService.countUsers()).thenReturn(42L);

        mockMvc.perform(get("/api/accounts/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(42L));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN", username = "adminUser")
    void getAccountDistribution_shouldReturnDistribution() throws Exception {
        AccountDistributionDTO dto1 = new AccountDistributionDTO(AccountType.CURRENT_ACCOUNT, 25L);
        AccountDistributionDTO dto2 = new AccountDistributionDTO(AccountType.SAVING_ACCOUNT, 17L);
        
        when(accountService.getAccountDistribution()).thenReturn(java.util.Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/accounts/stats/distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CURRENT_ACCOUNT"))
                .andExpect(jsonPath("$[0].count").value(25L))
                .andExpect(jsonPath("$[1].type").value("SAVING_ACCOUNT"))
                .andExpect(jsonPath("$[1].count").value(17L));
    }

    @Test
    @WithMockUser
    void updateBalance_AddOperation_shouldUpdateBalance() throws Exception {
        doNothing().when(accountService).updateBalance(1L, 500.0, "ADD");

        String requestBody = "{\"amount\": 500.0, \"operation\": \"ADD\"}";

        mockMvc.perform(post("/api/accounts/1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateBalance_SubtractOperation_shouldUpdateBalance() throws Exception {
        doNothing().when(accountService).updateBalance(1L, 200.0, "SUBTRACT");

        String requestBody = "{\"amount\": 200.0, \"operation\": \"SUBTRACT\"}";

        mockMvc.perform(post("/api/accounts/1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getAccountByIban_shouldReturnAccount() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setIban("FR7612345678901234567890123");
        
        when(accountService.getAccountByIBAN("FR7612345678901234567890123")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/accounts/iban/FR7612345678901234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.iban").value("FR7612345678901234567890123"));
    }

    @Test
    @WithMockUser
    void createAccountOnRegister_shouldCreateAccount() throws Exception {
        BankAccountRequestDTO requestDTO = new BankAccountRequestDTO();
        requestDTO.setType(AccountType.CURRENT_ACCOUNT);
        requestDTO.setUserId(1L);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(AccountType.CURRENT_ACCOUNT);
        responseDTO.setUserId(1L);

        when(accountService.addAccount(any(BankAccountRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    @WithMockUser
    void getAccountTransactions_shouldReturnTransactions() throws Exception {
        TransactionResponseDTO transaction = new TransactionResponseDTO();
        transaction.setId(1L);
        transaction.setAccountId(1L);
        transaction.setType("CREDIT");
        
        when(accountService.getTransactionsForAccount(anyLong(), any())).thenReturn(Collections.singletonList(transaction));

        mockMvc.perform(get("/api/accounts/1/transactions")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value("CREDIT"));
    }

    @Test
    @WithMockUser
    void createAccountTransaction_shouldCreateTransaction() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setAmount(java.math.BigDecimal.valueOf(100.0));
        requestDTO.setType("DEPOSIT");

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setAccountId(1L);
        responseDTO.setType("CREDIT");

        when(accountService.createTransactionForAccount(anyLong(), any(TransactionRequestDTO.class), any()))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/accounts/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("CREDIT"));
    }

    @Test
    void testBalanceUpdateRequest() {
        AccountController.BalanceUpdateRequest request = new AccountController.BalanceUpdateRequest();
        request.setAmount(100.0);
        request.setOperation("ADD");

        org.assertj.core.api.Assertions.assertThat(request.getAmount()).isEqualTo(100.0);
        org.assertj.core.api.Assertions.assertThat(request.getOperation()).isEqualTo("ADD");
    }
}
