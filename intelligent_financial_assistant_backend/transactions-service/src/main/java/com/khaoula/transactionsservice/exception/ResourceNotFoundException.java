package com.khaoula.transactionsservice.exception;

/**
 * @author radouane
 **/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
