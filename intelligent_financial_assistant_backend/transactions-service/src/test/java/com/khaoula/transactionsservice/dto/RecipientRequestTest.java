package com.khaoula.transactionsservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipientRequestTest {

    @Test
    void testAllArgsConstructor() {
        RecipientRequest request = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");

        assertEquals("John Doe", request.getFullName());
        assertEquals("FR7612345678901234567890123", request.getIban());
        assertEquals("MyBank", request.getBank());
    }

    @Test
    void testNoArgsConstructor() {
        RecipientRequest request = new RecipientRequest();
        
        assertNotNull(request);
    }

    @Test
    void testSetters() {
        RecipientRequest request = new RecipientRequest();
        
        request.setFullName("Jane Doe");
        request.setIban("DE89370400440532013000");
        request.setBank("TestBank");

        assertEquals("Jane Doe", request.getFullName());
        assertEquals("DE89370400440532013000", request.getIban());
        assertEquals("TestBank", request.getBank());
    }

    @Test
    void testToString() {
        RecipientRequest request = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");
        
        String toString = request.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("John Doe"));
    }

    @Test
    void testEquals() {
        RecipientRequest request1 = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");
        RecipientRequest request2 = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");
        RecipientRequest request3 = new RecipientRequest("Jane Doe", "DE89370400440532013000", "OtherBank");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1, request1);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());
    }

    @Test
    void testHashCode() {
        RecipientRequest request1 = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");
        RecipientRequest request2 = new RecipientRequest("John Doe", "FR7612345678901234567890123", "MyBank");

        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
