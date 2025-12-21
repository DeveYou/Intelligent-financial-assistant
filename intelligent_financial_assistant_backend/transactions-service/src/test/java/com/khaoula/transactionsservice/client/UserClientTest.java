package com.khaoula.transactionsservice.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserClientTest {

    @Test
    void testUserDetails() {
        UserClient.UserDetails userDetails = new UserClient.UserDetails();
        userDetails.setId(1L);
        userDetails.setFirstName("John");
        userDetails.setLastName("Doe");
        userDetails.setEmail("test@example.com");

        assertEquals(1L, userDetails.getId());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals("test@example.com", userDetails.getEmail());
    }
}
