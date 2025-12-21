package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.service.AuthService;
import com.aitsaid.authservice.security.JwtUtil;
import com.aitsaid.authservice.service.UserDetailsServiceImpl;
import com.aitsaid.authservice.repositories.TokenBlockListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenBlockListRepository tokenBlockListRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");

        RegisterResponse response = new RegisterResponse("test@example.com", "John", "Doe", "Registration successful");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        LoginResponse response = new LoginResponse("token", "test@example.com", "John", "Doe", 1L, "Address", "1234567890", "CIN123", "Login successful");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    @WithMockUser
    void logout_Success() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Logged out successfully"));
    }

    @Test
    @WithMockUser
    void validateToken_Success() throws Exception {
        when(authService.validateToken(anyString())).thenReturn(true);

        mockMvc.perform(get("/auth/validate-token")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void logout_NullAuth_Success() throws Exception {
        doNothing().when(authService).logout(anyString());

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void validateToken_NullAuth_Success() throws Exception {
        when(authService.validateToken(anyString())).thenReturn(true);

        mockMvc.perform(get("/auth/validate-token")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
