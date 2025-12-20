package com.khaoula.recipientservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void defaultConstructor_CreatesInstance() {
        ApiResponse<String> response = new ApiResponse<>();
        assertNotNull(response);
    }

    @Test
    void parameterizedConstructor_SetsAllFields() {
        ApiResponse<String> response = new ApiResponse<>(true, "Success message", "Test data");

        assertTrue(response.isSuccess());
        assertEquals("Success message", response.getMessage());
        assertEquals("Test data", response.getData());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        ApiResponse<Integer> response = new ApiResponse<>();

        response.setSuccess(true);
        assertTrue(response.isSuccess());

        response.setMessage("Test message");
        assertEquals("Test message", response.getMessage());

        response.setData(42);
        assertEquals(42, response.getData());
    }

    @Test
    void successWithData_CreatesSuccessResponse() {
        Integer data = 123;
        ApiResponse<Integer> response = ApiResponse.success(data);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void successWithMessage_CreatesSuccessResponseWithoutData() {
        ApiResponse<Object> response = ApiResponse.success("Operation completed");

        assertTrue(response.isSuccess());
        assertEquals("Operation completed", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void error_CreatesErrorResponse() {
        ApiResponse<Object> response = ApiResponse.error("Error occurred");

        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void jsonSerialization_ExcludesNullFields() throws Exception {
        ApiResponse<String> response = ApiResponse.error("Error message");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);

        // Verify that null data field is not included
        assertFalse(json.contains("\"data\""));
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("\"message\":\"Error message\""));
    }

    @Test
    void jsonSerialization_IncludesNonNullFields() throws Exception {
        ApiResponse<String> response = new ApiResponse<>(true, "Success", "Test data");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);

        // Verify all fields are included when not null
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"message\":\"Success\""));
        assertTrue(json.contains("\"data\":\"Test data\""));
    }

    @Test
    void genericType_WorksWithDifferentTypes() {
        ApiResponse<Integer> intResponse = new ApiResponse<>(true, "Success", 123);
        assertEquals(123, intResponse.getData());

        ApiResponse<String> stringResponse = new ApiResponse<>(true, "Success", "text");
        assertEquals("text", stringResponse.getData());

        ApiResponse<Boolean> boolResponse = new ApiResponse<>(true, "Success", true);
        assertEquals(true, boolResponse.getData());
    }
}
