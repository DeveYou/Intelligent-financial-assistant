package com.aitsaid.authservice.service;

import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.entities.TokenBlockList;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.repositories.TokenBlockListRepository;
import com.aitsaid.authservice.repositories.UserRepository;
import com.aitsaid.authservice.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author radouane
 **/
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenBlockListRepository tokenBlockListRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       TokenBlockListRepository tokenBlockListRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.tokenBlockListRepository = tokenBlockListRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new RuntimeException("Email already exists");
        }

        User user = UserMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return new RegisterResponse(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName(), "Registration successful");
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), "Login successful");
    }

    public void logout(String token) {

        try {

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token != null && !token.isEmpty() && jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                TokenBlockList blockedToken = new TokenBlockList();
                blockedToken.setToken(token);
                blockedToken.setUser(user);

                tokenBlockListRepository.save(blockedToken);
            }
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            throw new RuntimeException("Logout failed " + e.getMessage());
        }

    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }

    public boolean isTokenBlocked(String token) {
        return tokenBlockListRepository.existsByToken(token);
    }
}
