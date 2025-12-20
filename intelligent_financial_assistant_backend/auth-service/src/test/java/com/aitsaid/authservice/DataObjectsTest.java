package com.aitsaid.authservice;

import com.aitsaid.authservice.dtos.*;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.TokenBlockList;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.handlers.ErrorResponse;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataObjectsTest {

    @Test
    void testDataObjects() {
        // BankAccountRequestDTO
        BankAccountRequestDTO bankAccountRequestDTO = new BankAccountRequestDTO();
        bankAccountRequestDTO.setType("CURRENT");
        bankAccountRequestDTO.setUserId(1L);
        assertEquals("CURRENT", bankAccountRequestDTO.getType());
        assertEquals(1L, bankAccountRequestDTO.getUserId());

        // LoginRequest
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password");
        assertEquals("test@test.com", loginRequest.getEmail());
        assertEquals("password", loginRequest.getPassword());

        // LoginResponse
        LoginResponse loginResponse = new LoginResponse("token", "test@test.com", "John", "Doe", 1L, "123 Street", "123456789", "CIN123", "Success");
        assertEquals("token", loginResponse.getToken());

        // RegisterRequest
        RegisterRequest registerRequest = new RegisterRequest("John", "Doe", "CIN123", "test@test.com", "password", "123456789", "123 Street");
        assertEquals("John", registerRequest.getFirstName());

        // RegisterResponse
        RegisterResponse registerResponse = new RegisterResponse("test@test.com", "John", "Doe", "Success");
        assertEquals("Success", registerResponse.getMessage());

        // UpdateProfileRequest
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCin("CIN456");
        assertEquals("CIN456", updateProfileRequest.getCin());

        // UpdateUserRequest
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("Jane");
        assertEquals("Jane", updateUserRequest.getFirstName());

        // UserDetails
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        assertEquals(1L, userDetails.getId());

        // TokenBlockList
        TokenBlockList tokenBlockList = new TokenBlockList(1L, "token", LocalDateTime.now(), new User());
        assertNotNull(tokenBlockList.getToken());

        // User
        User user = new User(1L, "John", "Doe", "CIN123", "test@test.com", "pass", "123", "addr", Role.ROLE_USER, LocalDateTime.now(), true, null);
        assertNotNull(user.getFirstName());

        // ErrorResponse
        ErrorResponse errorResponse = ErrorResponse.builder().status(404).message("Not Found").build();
        assertEquals(404, errorResponse.getStatus());
    }
}
