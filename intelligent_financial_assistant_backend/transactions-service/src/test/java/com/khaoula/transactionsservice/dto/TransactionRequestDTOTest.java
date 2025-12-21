package com.khaoula.transactionsservice.dto;

import com.khaoula.transactionsservice.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionRequestDTOTest {

    @Test
    void testTransactionRequestDTO() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setBankAccountId(1L);
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setRecipientIban("FR7612345678901234567890123");
        dto.setRecipientId(1L);
        dto.setRecipientName("John Doe");
        dto.setReason("Test Transaction");

        assertEquals(1L, dto.getBankAccountId());
        assertEquals(TransactionType.DEPOSIT, dto.getType());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals("FR7612345678901234567890123", dto.getRecipientIban());
        assertEquals(1L, dto.getRecipientId());
        assertEquals("John Doe", dto.getRecipientName());
        assertEquals("Test Transaction", dto.getReason());
    }
}
