package com.lachguer.accountservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lachguer.accountservice.dto.BankAccountRequestDTO;
import com.lachguer.accountservice.dto.BankAccountResponseDTO;
import com.lachguer.accountservice.dto.BankAccountUpdateDTO;
import com.lachguer.accountservice.enums.AccountType;
import com.lachguer.accountservice.model.BankAccount;
import com.lachguer.accountservice.model.CurrentAccount;
import com.lachguer.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAccounts_shouldReturnListOfAccounts() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        when(accountService.getAccounts()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAccountById_shouldReturnAccount() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        when(accountService.getAccountById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAccountById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(accountService.getAccountById(1L)).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountsByUserId_shouldReturnUserAccounts() throws Exception {
        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        when(accountService.getAccountsByUserId(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void addAccount_shouldCreateAccount() throws Exception {
        BankAccountRequestDTO requestDTO = new BankAccountRequestDTO();
        requestDTO.setType(AccountType.CURRENT_ACCOUNT);
        requestDTO.setBalance(1000.0);
        requestDTO.setUserId(1L);

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(AccountType.CURRENT_ACCOUNT);
        responseDTO.setBalance(1000.0);

        when(accountService.addAccount(any(BankAccountRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void updateAccount_shouldUpdateAccount() throws Exception {
        // Créez un DTO pour la mise à jour
        BankAccountUpdateDTO updateDTO = new BankAccountUpdateDTO();
        updateDTO.setBalance(1500.0);
        updateDTO.setIsActive(true);
        updateDTO.setOverDraft(200.0); // Si c'est un CurrentAccount

        // Créez un CurrentAccount qui sera retourné par le service
        CurrentAccount updatedAccount = new CurrentAccount();
        updatedAccount.setId(1L);
        updatedAccount.setBalance(1500.0);
        updatedAccount.setOverDraft(200.0);
        updatedAccount.setIsActive(true);
        updatedAccount.setAccountType(AccountType.CURRENT_ACCOUNT);

        // Mockez avec BankAccountUpdateDTO, pas BankAccount
        when(accountService.updateAccount(anyLong(), any(BankAccountUpdateDTO.class)))
                .thenReturn(updatedAccount);

        // Utilisez PATCH (pas PUT) et envoyez le DTO
        mockMvc.perform(patch("/api/accounts/1")  // PATCH pour les mises à jour partielles
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))  // Envoyez le DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0))
                .andExpect(jsonPath("$.overDraft").value(200.0))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void deleteAccount_shouldDeleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isOk());
    }
}
