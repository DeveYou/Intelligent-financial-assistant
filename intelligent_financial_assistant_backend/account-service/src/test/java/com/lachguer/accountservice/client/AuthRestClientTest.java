package com.lachguer.accountservice.client;

import com.lachguer.accountservice.dto.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthRestClientTest {

    @Test
    void testUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstname("John");
        userDTO.setLastname("Doe");
        userDTO.setEmail("test@example.com");

        assertEquals(1L, userDTO.getId());
        assertEquals("John", userDTO.getFirstname());
        assertEquals("Doe", userDTO.getLastname());
        assertEquals("test@example.com", userDTO.getEmail());
    }
}
