package com.aitsaid.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApplicationTest {

    @Test
    void main() {
        // This test ensures the application context loads and the main method can be called
        // We use -Dspring.main.web-application-type=none to speed up or just run normally
        assertDoesNotThrow(() -> AuthServiceApplication.main(new String[]{}));
    }
}
