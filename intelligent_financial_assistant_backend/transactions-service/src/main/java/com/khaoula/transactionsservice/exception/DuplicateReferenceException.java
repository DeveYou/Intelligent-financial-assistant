package com.khaoula.transactionsservice.exception;

public class DuplicateReferenceException extends RuntimeException {

    public DuplicateReferenceException(String message) {
        super(message);
    }
}

