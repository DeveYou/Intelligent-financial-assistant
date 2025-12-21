package com.khaoula.transactionsservice.dto;

import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionFilterDTOTest {

    @Test
    void testTransactionFilterDTO() {
        TransactionFilterDTO dto = new TransactionFilterDTO();
        dto.setUserId(1L);
        dto.setBankAccountId(1L);
        dto.setType(TransactionType.DEPOSIT);
        dto.setStatus(TransactionStatus.COMPLETED);
        dto.setStartDate(OffsetDateTime.now());
        dto.setEndDate(OffsetDateTime.now());
        dto.setPage(1);
        dto.setSize(10);
        dto.setSortBy("amount");
        dto.setSortDirection("ASC");

        assertEquals(1L, dto.getUserId());
        assertEquals(1L, dto.getBankAccountId());
        assertEquals(TransactionType.DEPOSIT, dto.getType());
        assertEquals(TransactionStatus.COMPLETED, dto.getStatus());
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getEndDate());
        assertEquals(1, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("amount", dto.getSortBy());
        assertEquals("ASC", dto.getSortDirection());
    }
}
