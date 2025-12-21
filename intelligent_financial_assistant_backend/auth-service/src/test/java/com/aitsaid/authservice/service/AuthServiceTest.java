package com.aitsaid.authservice.service;

import com.aitsaid.authservice.clients.AccountRestClient;
import com.aitsaid.authservice.dtos.BankAccountRequestDTO;
import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.entities.TokenBlockList;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.exceptions.EmailAlreadyExistsException;
import com.aitsaid.authservice.exceptions.InvalidCredentialsException;
import com.aitsaid.authservice.exceptions.InvalidTokenException;
import com.aitsaid.authservice.exceptions.LogoutFailedException;
import com.aitsaid.authservice.exceptions.UserNotAuthenticatedException;
import com.aitsaid.authservice.exceptions.UserNotFoundException;
import com.aitsaid.authservice.repositories.TokenBlockListRepository;
import com.aitsaid.authservice.repositories.UserRepository;
import com.aitsaid.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlockListRepository tokenBlockListRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AccountRestClient accountRestClient;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accountRestClient.createAccount(any(BankAccountRequestDTO.class))).thenReturn(null);

        RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
        verify(accountRestClient, times(1)).createAccount(any(BankAccountRequestDTO.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwtToken");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void logout_Success() {
        String token = "Bearer validToken";
        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);
        when(tokenBlockListRepository.existsByToken("validToken")).thenReturn(false);
        when(jwtUtil.extractUsername("validToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        authService.logout(token);

        verify(tokenBlockListRepository, times(1)).save(any(TokenBlockList.class));
    }

    @Test
    void logout_InvalidToken() {
        String token = "Bearer invalidToken";
        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.logout(token));
    }

    @Test
    void validateToken_Valid() {
        String token = "Bearer validToken";
        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);
        when(tokenBlockListRepository.existsByToken("validToken")).thenReturn(false);
        when(jwtUtil.extractUsername("validToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean isValid = authService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_Invalid() {
        String token = "Bearer invalidToken";
        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);

        boolean isValid = authService.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void logout_NullToken() {
        assertThrows(InvalidTokenException.class, () -> authService.logout(null));
    }

    @Test
    void logout_EmptyToken() {
        assertThrows(InvalidTokenException.class, () -> authService.logout(""));
    }

    @Test
    void logout_InvalidPrefix() {
        String token = "invalidPrefix tokensuffix";
        // After prefix removal (none), it checks validity of "invalidPrefix tokensuffix"
        when(jwtUtil.isTokenValid(token)).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> authService.logout(token));
    }

    @Test
    void logout_EmptyTokenAfterPrefix() {
        assertThrows(InvalidTokenException.class, () -> authService.logout("Bearer "));
    }

    @Test
    void logout_TokenAlreadyBlocked() {
        String token = "Bearer blockedToken";
        when(jwtUtil.isTokenValid("blockedToken")).thenReturn(true);
        when(tokenBlockListRepository.existsByToken("blockedToken")).thenReturn(true);

        assertThrows(InvalidTokenException.class, () -> authService.logout(token));
    }

    @Test
    void logout_UserNotFound() {
        String token = "Bearer validToken";
        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);
        when(tokenBlockListRepository.existsByToken("validToken")).thenReturn(false);
        when(jwtUtil.extractUsername("validToken")).thenReturn("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.logout(token));
    }

    @Test
    void logout_GeneralException() {
        String token = "Bearer validToken";
        when(jwtUtil.isTokenValid("validToken")).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(LogoutFailedException.class, () -> authService.logout(token));
    }

    @Test
    void getCurrentUser_Success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_UserNotFound() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.getCurrentUser());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertThrows(UserNotAuthenticatedException.class, () -> authService.getCurrentUser());
    }

    @Test
    void validateToken_NullToken() {
        assertFalse(authService.validateToken(null));
    }

    @Test
    void validateToken_EmptyToken() {
        assertFalse(authService.validateToken(""));
    }

    @Test
    void validateToken_EmptyAfterPrefix() {
        assertFalse(authService.validateToken("Bearer "));
    }

    @Test
    void validateToken_BlockedToken() {
        String token = "Bearer blockedToken";
        when(jwtUtil.isTokenValid("blockedToken")).thenReturn(true);
        when(tokenBlockListRepository.existsByToken("blockedToken")).thenReturn(true);

        assertFalse(authService.validateToken(token));
    }

    @Test
    void validateToken_Exception() {
        String token = "Bearer validToken";
        when(jwtUtil.isTokenValid("validToken")).thenThrow(new RuntimeException("Unexpected"));

        assertFalse(authService.validateToken(token));
    }

    @Test
    void login_GeneralException() {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Database down"));
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }
}
