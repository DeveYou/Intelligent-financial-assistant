package com.lachguer.accountservice.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TransactionRequestDTOTest {

    @Test
    void testTransactionRequestDTO() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        
        dto.setAccountId(1L);
        dto.setUserId(2L);
        dto.setAmount(amount);
        dto.setType("CREDIT");
        dto.setDescription("Test Transaction");

        assertEquals(1L, dto.getAccountId());
        assertEquals(2L, dto.getUserId());
        assertEquals(amount, dto.getAmount());
        assertEquals("CREDIT", dto.getType());
        assertEquals("Test Transaction", dto.getDescription());
    }
}
