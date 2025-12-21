package com.khaoula.recipientservice.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecipientResponseTest {

    @Test
    void defaultConstructor_CreatesInstance() {
        RecipientResponse response = new RecipientResponse();
        assertNotNull(response);
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        RecipientResponse response = new RecipientResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(1L);
        assertEquals(1L, response.getId());

        response.setBank("MyBank");
        assertEquals("MyBank", response.getBank());

        response.setIban("FR7612345678901234567890123");
        assertEquals("FR7612345678901234567890123", response.getIban());

        response.setFullName("John Doe");
        assertEquals("John Doe", response.getFullName());

        response.setCreatedAt(now);
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void allFields_CanBeSetAndRetrieved() {
        RecipientResponse response = new RecipientResponse();
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        response.setId(123L);
        response.setBank("Test Bank");
        response.setIban("FR7698765432109876543210987");
        response.setFullName("Jane Doe");
        response.setCreatedAt(timestamp);

        assertEquals(123L, response.getId());
        assertEquals("Test Bank", response.getBank());
        assertEquals("FR7698765432109876543210987", response.getIban());
        assertEquals("Jane Doe", response.getFullName());
        assertEquals(timestamp, response.getCreatedAt());
    }

    @Test
    void jsonSerialization_UsesCorrectPropertyNames() throws Exception {
        RecipientResponse response = new RecipientResponse();
        response.setId(1L);
        response.setFullName("John Doe");
        response.setIban("FR7612345678901234567890123");
        response.setBank("MyBank");
        response.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDateTime

        String json = mapper.writeValueAsString(response);

        // Verify JSON property names
        assertTrue(json.contains("\"full_name\""));
        assertTrue(json.contains("\"created_at\""));
        assertTrue(json.contains("\"John Doe\""));
    }
}
