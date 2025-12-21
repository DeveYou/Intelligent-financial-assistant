package com.khaoula.transactionsservice.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountClientTest {

    @Test
    void testAccountResponse() {
        AccountClient.AccountResponse response = new AccountClient.AccountResponse();
        response.setId(1L);
        response.setIban("FR7612345678901234567890123");
        response.setBalance(1000.0);
        response.setUserId(1L);
        response.setIsActive(true);

        assertEquals(1L, response.getId());
        assertEquals("FR7612345678901234567890123", response.getIban());
        assertEquals(1000.0, response.getBalance());
        assertEquals(1L, response.getUserId());
        assertEquals(true, response.getIsActive());
    }

    @Test
    void testBalanceUpdateRequest() {
        AccountClient.BalanceUpdateRequest request = new AccountClient.BalanceUpdateRequest(100.0, "ADD");

        assertEquals(100.0, request.getAmount());
        assertEquals("ADD", request.getOperation());
    }
}
