package com.lachguer.accountservice.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TransactionResponseDTOTest {

    @Test
    void testTransactionResponseDTO() {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        Date now = new Date();
        
        dto.setId(1L);
        dto.setAccountId(2L);
        dto.setUserId(3L);
        dto.setCreatedAt(now);
        dto.setAmount(amount);
        dto.setType("DEBIT");
        dto.setDescription("Test");
        
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getAccountId());
        assertEquals(3L, dto.getUserId());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(amount, dto.getAmount());
        assertEquals("DEBIT", dto.getType());
        assertEquals("Test", dto.getDescription());
    }
}
