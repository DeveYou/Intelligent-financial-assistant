package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/users")
public class UserControllerUser {

    private static final Logger log = LoggerFactory.getLogger(UserControllerUser.class);
    private final UserService userService;

    public UserControllerUser(UserService userService) {
        this.userService = userService;
    }

    /**
     * Mettre à jour son propre profil
     */
    @PatchMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDetails> updateUserProfile(
            @RequestBody UpdateProfileRequest updateRequest,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = (User) authentication.getPrincipal();
        log.info("User {} updating their profile", currentUser.getEmail());

        User updatedUser = userService.updateUserProfile(currentUser.getId(), updateRequest);
        return ResponseEntity.ok(UserMapper.userToUserDetails(updatedUser));
    }

    /**
     * Récupérer son propre profil
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDetails> getMyProfile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).build();
        }
        User currentUser = (User) authentication.getPrincipal();
        log.debug("User {} retrieving their profile", currentUser.getEmail());
        return ResponseEntity.ok(UserMapper.userToUserDetails(currentUser));
    }
}
