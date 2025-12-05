package com.khaoula.transactionsservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicateReferenceExceptionTest {

    @Test
    void constructor_shouldStoreMessage() {
        DuplicateReferenceException ex = new DuplicateReferenceException("Duplicate reference");
        assertEquals("Duplicate reference", ex.getMessage());
    }
}
