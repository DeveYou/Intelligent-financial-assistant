package com.khaoula.transactionsservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidTransaction_shouldReturnBadRequest() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid amount");

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidTransaction(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("Invalid amount", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleDuplicateReference_shouldReturnConflict() {
        DuplicateReferenceException ex = new DuplicateReferenceException("Ref already exists");

        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateReference(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.get("status"));
        assertEquals("Conflict", body.get("error"));
        assertEquals("Ref already exists", body.get("message"));
    }

    @Test
    void handleValidation_shouldReturnFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "amount", "must be greater than 0"));
        bindingResult.addError(new FieldError("target", "bankAccountId", "must not be null"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null,
                bindingResult
        );

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Validation failed", body.get("error"));
        assertTrue(body.containsKey("fieldErrors"));

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertEquals(2, fieldErrors.size());
        assertEquals("must be greater than 0", fieldErrors.get("amount"));
        assertEquals("must not be null", fieldErrors.get("bankAccountId"));
    }

    @Test
    void handleOther_shouldReturnInternalServerError() {
        Exception ex = new Exception("boom");

        ResponseEntity<Map<String, Object>> response = handler.handleOther(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertTrue(((String) body.get("message")).contains("boom"));
    }
}
