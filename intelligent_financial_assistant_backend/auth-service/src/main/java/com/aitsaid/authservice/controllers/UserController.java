package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


/**
 * @author radouane
 **/
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("admin/users")
    public ResponseEntity<List<UserDetails>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("users/{userId}/profile")
    public ResponseEntity<UserDetails> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest updateRequest,
            Authentication authentication) throws AccessDeniedException {

        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que votre propre profil");
        }

        User updatedUser = userService.updateUserProfile(userId, updateRequest);
        return ResponseEntity.ok(UserMapper.userToUserDetails(updatedUser));
    }

}
