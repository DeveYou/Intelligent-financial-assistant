package com.khaoula.recipientservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.khaoula.recipientservice.repository.RecipientRepository;
import com.khaoula.recipientservice.service.RecipientService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipientService recipientService;

    @MockBean
    private RecipientRepository recipientRepository;

    @Test
    void actuatorHealthEndpoint_IsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isNotFound()); // Endpoint not configured in test, but not blocked by security
    }

    @Test
    void protectedEndpoint_WithoutAuthentication_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/recipients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void protectedEndpoint_WithAuthentication_IsNotBlocked() throws Exception {
        // With authentication, security doesn't block (may return 404 if endpoint doesn't exist in test context)
        mockMvc.perform(get("/api/recipients"))
                .andExpect(status().isNotFound());
    }

    @Test
    void securityConfig_BeansAreCreated() {
        // Test that security configuration is loaded
        assertNotNull(mockMvc);
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Object should not be null");
        }
    }
}
