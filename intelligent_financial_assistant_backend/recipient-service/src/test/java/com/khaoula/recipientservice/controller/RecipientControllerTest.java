package com.khaoula.recipientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaoula.recipientservice.dto.RecipientRequest;
import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.repository.RecipientRepository;
import com.khaoula.recipientservice.service.RecipientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipientController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simplicity in unit tests
class RecipientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipientService recipientService;

    @MockBean
    private RecipientRepository recipientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "1")
    void getRecipientList_Success() throws Exception {
        Recipient recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setCreatedAt(LocalDateTime.now());

        when(recipientService.getRecipientList(1L)).thenReturn(Collections.singletonList(recipient));

        mockMvc.perform(get("/api/recipients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].full_name").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "1")
    void addRecipient_Success() throws Exception {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("John Doe");
        request.setIban("FR7612345678901234567890123");
        request.setBank("MyBank");

        Recipient recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setCreatedAt(LocalDateTime.now());

        when(recipientService.addRecipient(any(Recipient.class), anyLong())).thenReturn(recipient);

        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.full_name").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "1")
    void updateRecipient_Success() throws Exception {
        RecipientRequest request = new RecipientRequest();
        request.setFullName("Jane Doe");
        request.setIban("FR7698765432109876543210987");
        request.setBank("OtherBank");

        Recipient recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("Jane Doe");
        recipient.setIban("FR7698765432109876543210987");
        recipient.setBank("OtherBank");
        recipient.setCreatedAt(LocalDateTime.now());

        when(recipientService.updateRecipient(anyLong(), any(Recipient.class), anyLong())).thenReturn(recipient);

        mockMvc.perform(put("/api/recipients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.full_name").value("Jane Doe"));
    }

    @Test
    @WithMockUser(username = "1")
    void deleteRecipient_Success() throws Exception {
        doNothing().when(recipientService).deleteRecipient(anyLong(), anyLong());

        mockMvc.perform(delete("/api/recipients/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recipient deleted successfully"));
    }

    @Test
    @WithMockUser(username = "1")
    void getByIban_Success() throws Exception {
        Recipient recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setCreatedAt(LocalDateTime.now());

        when(recipientService.getByIban(anyString())).thenReturn(Optional.of(recipient));

        mockMvc.perform(get("/api/recipients/iban/FR7612345678901234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.full_name").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "1")
    void getRecipientById_Success() throws Exception {
        Recipient recipient = new Recipient();
        recipient.setId(1L);
        recipient.setUserId(1L);
        recipient.setFullName("John Doe");
        recipient.setIban("FR7612345678901234567890123");
        recipient.setBank("MyBank");
        recipient.setCreatedAt(LocalDateTime.now());

        when(recipientRepository.findById(1L)).thenReturn(Optional.of(recipient));

        mockMvc.perform(get("/api/recipients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.full_name").value("John Doe"));
    }
}
