package com.aitsaid.authservice.service;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.exceptions.CinAlreadyExistsException;
import com.aitsaid.authservice.exceptions.EmailAlreadyExistsException;
import com.aitsaid.authservice.exceptions.UserNotFoundException;
import com.aitsaid.authservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.ROLE_USER);
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAllByRole(Role.ROLE_USER)).thenReturn(Collections.singletonList(user));

        List<UserDetails> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void countUsers_Success() {
        when(userRepository.countByRole(Role.ROLE_USER)).thenReturn(10L);

        long count = userService.countUsers();

        assertEquals(10L, count);
    }

    @Test
    void updateUserProfile_Success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setCin("CIN123");
        request.setAddress("New Address");
        request.setPhoneNumber("1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserProfile(1L, request);

        assertNotNull(updatedUser);
        assertEquals("CIN123", updatedUser.getCin());
        assertEquals("New Address", updatedUser.getAddress());
        assertEquals("1234567890", updatedUser.getPhoneNumber());
    }

    @Test
    void updateUserProfile_NotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfile(1L, request));
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, request);

        assertNotNull(updatedUser);
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("Jane", updatedUser.getFirstName());
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    void updateUser_CinAlreadyExists() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCin("EXISTING_CIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByCin("EXISTING_CIN")).thenReturn(true);

        assertThrows(CinAlreadyExistsException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDetails result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void updateUser_NotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_SameEmail_NoException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("test@example.com"); // existing email of same user

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, request);
        assertNotNull(updatedUser);
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void updateUser_SameCin_NoException() {
        user.setCin("CIN123");
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCin("CIN123"); // existing cin of same user

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, request);
        assertNotNull(updatedUser);
        verify(userRepository, never()).existsByCin(anyString());
    }
}
