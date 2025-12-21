package com.khaoula.transactionsservice.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DailyTransactionStatsTest {

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        DailyTransactionStats stats = new DailyTransactionStats(now, 10L);

        assertEquals(now, stats.getDate());
        assertEquals(10L, stats.getCount());
    }

    @Test
    void testNoArgsConstructor() {
        DailyTransactionStats stats = new DailyTransactionStats();
        
        assertNotNull(stats);
    }

    @Test
    void testSetters() {
        DailyTransactionStats stats = new DailyTransactionStats();
        Date testDate = new Date();
        
        stats.setDate(testDate);
        stats.setCount(25L);

        assertEquals(testDate, stats.getDate());
        assertEquals(25L, stats.getCount());
    }

    @Test
    void testToString() {
        Date now = new Date();
        DailyTransactionStats stats = new DailyTransactionStats(now, 10L);
        
        String toString = stats.toString();
        
        assertNotNull(toString);
    }

    @Test
    void testEquals() {
        Date date1 = new Date();
        DailyTransactionStats stats1 = new DailyTransactionStats(date1, 10L);
        DailyTransactionStats stats2 = new DailyTransactionStats(date1, 10L);
        DailyTransactionStats stats3 = new DailyTransactionStats(new Date(date1.getTime() + 1000), 20L);

        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
        assertEquals(stats1, stats1);
        assertNotEquals(stats1, null);
        assertNotEquals(stats1, new Object());
    }

    @Test
    void testHashCode() {
        Date date = new Date();
        DailyTransactionStats stats1 = new DailyTransactionStats(date, 10L);
        DailyTransactionStats stats2 = new DailyTransactionStats(date, 10L);

        assertEquals(stats1.hashCode(), stats2.hashCode());
    }
}
