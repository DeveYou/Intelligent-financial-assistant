package com.lachguer.accountservice.client;

import com.lachguer.accountservice.dto.TransactionResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionRestClientTest {

    @Test
    void testTransactionResponseDTO() {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1L);
        dto.setAccountId(1L);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setType("DEBIT");
        dto.setDescription("Test Transaction");

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getAccountId());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals("DEBIT", dto.getType());
        assertEquals("Test Transaction", dto.getDescription());
    }
}
