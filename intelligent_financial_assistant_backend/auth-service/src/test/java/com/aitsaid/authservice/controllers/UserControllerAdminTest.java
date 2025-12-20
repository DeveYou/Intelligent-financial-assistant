package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.service.AuthService;
import com.aitsaid.authservice.service.UserService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerAdmin.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    void createUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCin("CIN999");
        
        RegisterResponse response = new RegisterResponse("new@example.com", "F", "L", "Success");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/admin/users")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void countUsers_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());
        when(userService.countUsers()).thenReturn(10L);

        mockMvc.perform(get("/admin/users/count")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10L));
    }

    @Test
    void updateUser_Success() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        User user = new User();
        user.setId(1L);
        user.setEmail("updated@example.com");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/admin/users/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1")
                        .principal(auth))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserById_Success() throws Exception {
        UserDetails details = new UserDetails();
        details.setEmail("found@example.com");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList());

        when(userService.getUserById(1L)).thenReturn(details);

        mockMvc.perform(get("/admin/users/1")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("found@example.com"));
    }
}
