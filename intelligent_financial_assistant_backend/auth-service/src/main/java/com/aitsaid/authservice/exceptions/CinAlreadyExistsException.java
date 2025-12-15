package com.aitsaid.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CinAlreadyExistsException extends RuntimeException {
    public CinAlreadyExistsException(String cin) {
        super("CIN already exists: " + cin);
    }
}
