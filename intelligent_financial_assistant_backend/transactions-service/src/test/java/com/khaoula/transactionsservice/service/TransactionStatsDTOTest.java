package com.khaoula.transactionsservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionStatsDTOTest {

    @Test
    void testTransactionStatsDTO() {
        TransactionService.TransactionStatsDTO stats = new TransactionService.TransactionStatsDTO(
                100L, 10L, 80L, 10L, 5000.0, 5L
        );

        assertEquals(100L, stats.getTotalTransactions());
        assertEquals(10L, stats.getPendingTransactions());
        assertEquals(80L, stats.getCompletedTransactions());
        assertEquals(10L, stats.getFailedTransactions());
        assertEquals(5000.0, stats.getTotalVolume());
        assertEquals(5L, stats.getTodayTransactions());
    }
}
