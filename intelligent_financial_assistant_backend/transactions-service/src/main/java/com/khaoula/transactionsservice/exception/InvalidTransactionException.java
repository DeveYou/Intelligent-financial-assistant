package com.khaoula.transactionsservice.exception;

/**
 * @author radouane
 **/
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
