package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint public - Inscription
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.register(request));
    }


    /**
     * Endpoint public - Connexion
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint protégé - Déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, Authentication authentication) {
        if (authentication != null) {
            log.info("Logout request from user: {}", authentication.getName());
        } else {
            log.info("Logout request with null authentication");
        }
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Endpoint protégé - Validation de token
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token, Authentication authentication) {
        if (authentication != null) {
            log.debug("Token validation request from user: {}", authentication.getName());
        } else {
            log.debug("Token validation request with null authentication");
        }
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

}
