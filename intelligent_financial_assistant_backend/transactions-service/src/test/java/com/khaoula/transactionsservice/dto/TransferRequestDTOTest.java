package com.khaoula.transactionsservice.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferRequestDTOTest {

    @Test
    void testTransferRequestDTO() {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setBankAccountId(1L);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setRecipientIban("FR7612345678901234567890123");
        dto.setReason("Test Transfer");

        assertEquals(1L, dto.getBankAccountId());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals("FR7612345678901234567890123", dto.getRecipientIban());
        assertEquals("Test Transfer", dto.getReason());
    }
}
