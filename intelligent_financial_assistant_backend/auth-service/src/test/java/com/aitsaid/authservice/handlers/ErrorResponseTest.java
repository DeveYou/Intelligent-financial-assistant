package com.aitsaid.authservice.handlers;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void builder_CreatesErrorResponse() {
        // When
        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message("Not found")
                .timestamp(LocalDateTime.now())
                .build();

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Not found", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        // Given
        ErrorResponse response = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("Error");
        response.setTimestamp(timestamp);

        // Then
        assertEquals(500, response.getStatus());
        assertEquals("Error", response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void allArgsConstructor_CreatesObject() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ErrorResponse response = new ErrorResponse(timestamp, 400, "Bad Request", "Bad request");

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals("Bad request", response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void noArgsConstructor_CreatesObject() {
        // When
        ErrorResponse response = new ErrorResponse();

        // Then
        assertNotNull(response);
    }
}
