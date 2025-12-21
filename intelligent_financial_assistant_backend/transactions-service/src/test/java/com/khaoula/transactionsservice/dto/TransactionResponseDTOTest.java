package com.khaoula.transactionsservice.dto;

import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionResponseDTOTest {

    @Test
    void testTransactionResponseDTO() {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setBankAccountId(1L);
        dto.setReference("TXN-12345678");
        dto.setType(TransactionType.DEPOSIT);
        dto.setStatus(TransactionStatus.COMPLETED);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setRecipientId(1L);
        dto.setRecipientName("John Doe");
        dto.setRecipientIban("FR7612345678901234567890123");
        dto.setReason("Test Transaction");
        dto.setDate(OffsetDateTime.now());

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertEquals(1L, dto.getBankAccountId());
        assertEquals("TXN-12345678", dto.getReference());
        assertEquals(TransactionType.DEPOSIT, dto.getType());
        assertEquals(TransactionStatus.COMPLETED, dto.getStatus());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals(1L, dto.getRecipientId());
        assertEquals("John Doe", dto.getRecipientName());
        assertEquals("FR7612345678901234567890123", dto.getRecipientIban());
        assertEquals("Test Transaction", dto.getReason());
        assertNotNull(dto.getDate());
    }
}
