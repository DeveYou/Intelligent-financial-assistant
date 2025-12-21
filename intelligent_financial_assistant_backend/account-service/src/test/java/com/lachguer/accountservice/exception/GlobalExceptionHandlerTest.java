package com.lachguer.accountservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized access");
        ResponseEntity<java.util.Map<String, Object>> response = exceptionHandler.handleUnauthorized(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access", response.getBody().get("message"));
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response.getBody().get("error"));
    }

    @Test
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("Account not found");
        ResponseEntity<java.util.Map<String, Object>> response = exceptionHandler.handleRuntimeException(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found", response.getBody().get("message"));
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getBody().get("error"));
    }
}
