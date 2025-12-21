package com.aitsaid.authservice.mappers;

import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void registerRequestToUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCin("CIN123");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setPhoneNumber("123456789");
        request.setAddress("Street 1");

        User user = UserMapper.registerRequestToUser(request);

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("CIN123", user.getCin());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("123456789", user.getPhoneNumber());
        assertEquals("Street 1", user.getAddress());
        assertEquals(Role.ROLE_USER, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    void updateUserFromRequest_AllFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setCin("CIN456");
        request.setAddress("Street 2");
        request.setPhoneNumber("987654321");
        request.setEnabled(false);

        User user = new User();
        user.setFirstName("John");

        UserMapper.updateUserFromRequest(request, user);

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("CIN456", user.getCin());
        assertEquals("Street 2", user.getAddress());
        assertEquals("987654321", user.getPhoneNumber());
        assertFalse(user.isEnabled());
    }

    @Test
    void updateUserFromRequest_NullFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        // All fields null

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCin("CIN123");
        user.setAddress("Street 1");
        user.setPhoneNumber("123456789");
        user.setEnabled(true);

        UserMapper.updateUserFromRequest(request, user);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("CIN123", user.getCin());
        assertEquals("Street 1", user.getAddress());
        assertEquals("123456789", user.getPhoneNumber());
        assertTrue(user.isEnabled());
    }

    @Test
    void userToUserDetails_Success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCin("CIN123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("Street 1");
        user.setEnabled(true);

        UserDetails details = UserMapper.userToUserDetails(user);

        assertNotNull(details);
        assertEquals(1L, details.getId());
        assertEquals("John", details.getFirstName());
        assertEquals("test@example.com", details.getEmail());
        assertTrue(details.isEnabled());
    }

    @Test
    void privateConstructor_ThrowsException() throws Exception {
        Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals("Cannot instantiate utility class", exception.getCause().getMessage());
    }
}
