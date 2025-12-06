package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


/**
 * @author radouane
 **/
@RestController
@RequestMapping("/user/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerUser {

    private final UserService userService;

    /**
     * Mettre à jour son propre profil
     */
    @PatchMapping("/me/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDetails> updateUserProfile(
            @RequestBody UpdateProfileRequest updateRequest,
            Authentication authentication) {

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
        User currentUser = (User) authentication.getPrincipal();
        log.debug("User {} retrieving their profile", currentUser.getEmail());
        return ResponseEntity.ok(UserMapper.userToUserDetails(currentUser));
    }
}
