package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.service.AuthService;
import com.aitsaid.authservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserControllerAdmin {

    private static final Logger log = LoggerFactory.getLogger(UserControllerAdmin.class);
    private final UserService userService;
    private final AuthService authService;

    public UserControllerAdmin(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Créer un nouvel utilisateur (ADMIN uniquement)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RegisterResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Admin creating user: {}", request.getEmail());
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Récupérer tous les utilisateurs (ADMIN uniquement)
     */
    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDetails>> getAllUsers(Authentication authentication) {
        log.info("Admin {} retrieving all users", authentication.getName());
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Récupérer le nombre total d'utilisateurs (ADMIN uniquement)
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> countUsers(Authentication authentication) {
        log.info("Admin {} retrieving user count", authentication.getName());
        return ResponseEntity.ok(userService.countUsers());
    }

    /**
     * Mettre à jour un utilisateur (ADMIN uniquement)
     */
    @PatchMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDetails> updateUser(@PathVariable Long userId,
            @RequestBody UpdateUserRequest updateRequest, Authentication authentication) {
        log.info("Admin {} updating user ID: {}", authentication.getName(), userId);
        User user = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(UserMapper.userToUserDetails(user));
    }

    /**
     * Supprimer un utilisateur (ADMIN uniquement)
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Authentication authentication) {
        log.info("Admin {} deleting user ID: {}", authentication.getName(), userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer un utilisateur par ID (ADMIN uniquement)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long userId, Authentication authentication) {
        log.info("Admin {} retrieving  user ID: {}", authentication.getName(), userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

}
