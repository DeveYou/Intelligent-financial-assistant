package com.khaoula.recipientservice.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserClientTest {

    @Test
    void userResponse_DefaultConstructor_CreatesInstance() {
        UserClient.UserResponse response = new UserClient.UserResponse();
        assertNotNull(response);
    }

    @Test
    void userResponse_GettersAndSetters_WorkCorrectly() {
        UserClient.UserResponse response = new UserClient.UserResponse();

        // Test userId
        response.setUserId(123L);
        assertEquals(123L, response.getUserId());

        // Test email
        response.setEmail("test@example.com");
        assertEquals("test@example.com", response.getEmail());

        // Test valid
        response.setValid(true);
        assertTrue(response.isValid());

        response.setValid(false);
        assertFalse(response.isValid());
    }

    @Test
    void userResponse_AllFields_CanBeSetAndRetrieved() {
        UserClient.UserResponse response = new UserClient.UserResponse();
        
        response.setUserId(456L);
        response.setEmail("user@test.com");
        response.setValid(true);

        assertEquals(456L, response.getUserId());
        assertEquals("user@test.com", response.getEmail());
        assertTrue(response.isValid());
    }
}
