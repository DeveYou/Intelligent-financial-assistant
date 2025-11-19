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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository,
                       TokenBlockListRepository tokenBlockListRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.tokenBlockListRepository = tokenBlockListRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
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

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        TokenBlockList blockedToken = new TokenBlockList();
        blockedToken.setToken(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User principal) {
            blockedToken.setUser(principal);
        }

        tokenBlockListRepository.save(blockedToken);

        SecurityContextHolder.clearContext();
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
