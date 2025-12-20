package com.aitsaid.authservice.dtos;

import org.junit.jupiter.api.Test;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class DtoCoverageTest {

    private final Validator validator;

    public DtoCoverageTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void loginRequest_test() {
        LoginRequest dto = new LoginRequest();
        dto.setEmail("test@email.com");
        dto.setPassword("password");

        assertEquals("test@email.com", dto.getEmail());
        assertEquals("password", dto.getPassword());

        LoginRequest dto2 = new LoginRequest("test@email.com", "password");
        assertEquals("test@email.com", dto2.getEmail());
        
        LoginRequest invalid = new LoginRequest();
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
    }

    @Test
    void registerRequest_test() {
        RegisterRequest dto = new RegisterRequest();
        dto.setEmail("test@email.com");
        dto.setPassword("password123"); // > 6 chars
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setCin("AB123456");
        dto.setPhoneNumber("1234567890");
        dto.setAddress("Addr");

        assertEquals("test@email.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
        assertEquals("First", dto.getFirstName());
        assertEquals("Last", dto.getLastName());
        assertEquals("AB123456", dto.getCin());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals("Addr", dto.getAddress());

        RegisterRequest allArgs = new RegisterRequest("First", "Last", "AB123", "e@mail.com", "pass123", "phone", "addr");
        assertEquals("First", allArgs.getFirstName());
        assertEquals("addr", allArgs.getAddress());
    }

    @Test
    void loginResponse_test() {
        LoginResponse dto = new LoginResponse();
        dto.setToken("token");
        dto.setEmail("email");
        dto.setFirstName("first");
        dto.setLastName("last");
        dto.setId(1L);
        dto.setAddress("addr");
        dto.setPhoneNumber("phone");
        dto.setCin("cin");
        dto.setMessage("msg");
        dto.setType("Bearer");

        assertEquals("token", dto.getToken());
        assertEquals("email", dto.getEmail());
        assertEquals("first", dto.getFirstName());
        assertEquals("last", dto.getLastName());
        assertEquals(1L, dto.getId());
        assertEquals("addr", dto.getAddress());
        assertEquals("phone", dto.getPhoneNumber());
        assertEquals("cin", dto.getCin());
        assertEquals("msg", dto.getMessage());
        assertEquals("Bearer", dto.getType());

        LoginResponse allArgs = new LoginResponse("t", "e", "f", "l", 2L, "a", "p", "c", "m");
        assertEquals("t", allArgs.getToken());
        assertEquals("Bearer", allArgs.getType());
    }

    @Test
    void registerResponse_test() {
        RegisterResponse dto = new RegisterResponse();
        dto.setEmail("e");
        dto.setFirstName("f");
        dto.setLastName("l");
        dto.setMessage("m");

        assertEquals("e", dto.getEmail());
        assertEquals("f", dto.getFirstName());
        assertEquals("l", dto.getLastName());
        assertEquals("m", dto.getMessage());

        RegisterResponse allArgs = new RegisterResponse("e", "f", "l", "m");
        assertEquals("e", allArgs.getEmail());
    }
}
