package com.khaoula.recipientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.service.RecipientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecipientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipientService recipientService;

    @Mock
    private com.khaoula.recipientservice.client.UserClient userClient;

    @InjectMocks
    private RecipientController recipientController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipientController).build();
    }

    @Test
    void getRecipientList_returnsOk() throws Exception {
        Recipient r = new Recipient();
        r.setBank("Bank");
        r.setIban("GB00TESTIBAN000000");
        r.setFullName("Full");
        r.setCreatedAt(LocalDateTime.now());
        r.setUserId(1L);
        when(recipientService.getRecipientList(1L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/recipients").header("user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void addRecipient_returnsCreated() throws Exception {
        Recipient r = new Recipient();
        r.setBank("Bank");
        r.setIban("GB00TESTIBAN000000");
        r.setFullName("Full");
        r.setCreatedAt(LocalDateTime.now());
        r.setUserId(1L);
        when(recipientService.addRecipient(org.mockito.Mockito.any(Recipient.class), org.mockito.Mockito.eq(1L))).thenReturn(r);

        Recipient input = new Recipient();
        input.setFullName("Full");
        input.setIban("GB00TESTIBAN000000");

        mockMvc.perform(post("/api/recipients").header("user-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
