package com.khaoula.transactionsservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testTransactionEntity() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUserId(1L);
        transaction.setBankAccountId(1L);
        transaction.setReference("TXN-12345678");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setRecipientId(1L);
        transaction.setRecipientName("John Doe");
        transaction.setRecipientIban("FR7612345678901234567890123");
        transaction.setReason("Test Transaction");
        transaction.setDate(OffsetDateTime.now());

        assertEquals(1L, transaction.getId());
        assertEquals(1L, transaction.getUserId());
        assertEquals(1L, transaction.getBankAccountId());
        assertEquals("TXN-12345678", transaction.getReference());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(1L, transaction.getRecipientId());
        assertEquals("John Doe", transaction.getRecipientName());
        assertEquals("FR7612345678901234567890123", transaction.getRecipientIban());
        assertEquals("Test Transaction", transaction.getReason());
        assertNotNull(transaction.getDate());
    }
}
