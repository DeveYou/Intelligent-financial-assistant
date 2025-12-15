package com.khaoula.transactionsservice.exception;

/**
 * @author radouane
 **/
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
