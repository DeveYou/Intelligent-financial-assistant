package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.entities.Role;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.service.UserService;
import com.aitsaid.authservice.security.JwtUtil;
import com.aitsaid.authservice.service.UserDetailsServiceImpl;
import com.aitsaid.authservice.repositories.TokenBlockListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerUser.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private TokenBlockListRepository tokenBlockListRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyProfile_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/user/users/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
}

    @Test
    void getMyProfile_Unauthorized_NullAuth() throws Exception {
        mockMvc.perform(get("/user/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfile_Unauthorized_WrongPrincipal() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("not-a-user-object", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/user/users/me")
                        .principal(auth))
                .andExpect(status().isUnauthorized());
}

    @Test
    void updateUserProfile_Success() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setCin("CIN123");

        when(userService.updateUserProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(user);

        mockMvc.perform(patch("/user/users/me/profile")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
}

    @Test
    void updateUserProfile_Unauthorized_NullAuth() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        mockMvc.perform(patch("/user/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserProfile_Unauthorized_WrongPrincipal() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("not-a-user-object", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateProfileRequest request = new UpdateProfileRequest();
        mockMvc.perform(patch("/user/users/me/profile")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
}
}
