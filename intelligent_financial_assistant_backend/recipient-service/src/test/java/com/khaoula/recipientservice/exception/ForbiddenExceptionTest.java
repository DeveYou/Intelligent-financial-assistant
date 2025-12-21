package com.khaoula.recipientservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class ForbiddenExceptionTest {

    @Test
    void constructor_WithMessage_CreatesException() {
        String message = "Access forbidden";
        ForbiddenException exception = new ForbiddenException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void exception_IsRuntimeException() {
        ForbiddenException exception = new ForbiddenException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void exception_HasResponseStatusAnnotation() {
        ResponseStatus annotation = ForbiddenException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation);
        assertEquals(HttpStatus.FORBIDDEN, annotation.value());
    }
}
