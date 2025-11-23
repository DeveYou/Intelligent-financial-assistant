package com.aitsaid.authservice.exceptions;

/**
 * @author radouane
 **/
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
