package com.khaoula.transactionsservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidTransactionExceptionTest {

    @Test
    void constructor_shouldStoreMessage() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid transaction");
        assertEquals("Invalid transaction", ex.getMessage());
    }
}
