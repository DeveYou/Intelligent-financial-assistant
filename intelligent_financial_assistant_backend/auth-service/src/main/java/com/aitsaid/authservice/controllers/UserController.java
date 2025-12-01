package com.aitsaid.authservice.controllers;

import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.dtos.UpdateProfileRequest;
import com.aitsaid.authservice.dtos.UpdateUserRequest;
import com.aitsaid.authservice.dtos.UserDetails;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.service.FileStorageService;
import com.aitsaid.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorageService fileStorageService;

    @GetMapping("admin/users")
    public ResponseEntity<List<UserDetails>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("admin/users")
    public ResponseEntity<UserDetails> createUser(@RequestBody RegisterRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(UserMapper.userToUserDetails(user));
    }

    @PatchMapping("admin/users/{userId}")
    public ResponseEntity<UserDetails> updateUser(@PathVariable Long userId,
            @RequestBody UpdateUserRequest updateRequest) {
        User user = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(UserMapper.userToUserDetails(user));
    }

    @DeleteMapping("admin/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("admin/users/{userId}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
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

    @PostMapping("admin/users/{userId}/profile-image")
    public ResponseEntity<UserDetails> uploadUserProfileImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);
        String fileUrl = "http://localhost:8080/AUTH-SERVICE/files/" + fileName;

        User updatedUser = userService.updateUserProfileImage(userId, fileUrl);
        return ResponseEntity.ok(UserMapper.userToUserDetails(updatedUser));
    }

}
