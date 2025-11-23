package com.aitsaid.authservice.exceptions;

/**
 * @author radouane
 **/
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
