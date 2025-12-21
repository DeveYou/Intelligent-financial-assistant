package com.khaoula.transactionsservice.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipientClientTest {

    @Test
    void testRecipientResponse() {
        RecipientClient.RecipientResponse response = new RecipientClient.RecipientResponse();
        response.setId(1L);
        response.setBank("MyBank");
        response.setIban("FR7612345678901234567890123");
        response.setFullName("John Doe");

        assertEquals(1L, response.getId());
        assertEquals("MyBank", response.getBank());
        assertEquals("FR7612345678901234567890123", response.getIban());
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void testApiResponse() {
        RecipientClient.ApiResponse<String> response = new RecipientClient.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Success");
        response.setData("Data");

        assertEquals(true, response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("Data", response.getData());
    }
}
