package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


/**
 * @author radouane
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

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
        log.info("Logout request from user: {}", authentication.getName());
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Endpoint protégé - Validation de token
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token, Authentication authentication) {
        log.debug("Token validation request from user: {}", authentication.getName());
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

}
