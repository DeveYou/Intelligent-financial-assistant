package com.aitsaid.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author radouane
 **/
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class LogoutFailedException extends RuntimeException {
    public LogoutFailedException(Throwable cause) {
        super("Logout failed", cause);
    }
}
