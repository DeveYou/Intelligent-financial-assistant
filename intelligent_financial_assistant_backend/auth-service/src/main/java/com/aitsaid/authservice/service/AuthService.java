package com.aitsaid.authservice.service;

import com.aitsaid.authservice.clients.AccountRestClient;
import com.aitsaid.authservice.dtos.BankAccountRequestDTO;
import com.aitsaid.authservice.dtos.LoginResponse;
import com.aitsaid.authservice.dtos.RegisterResponse;
import com.aitsaid.authservice.dtos.LoginRequest;
import com.aitsaid.authservice.dtos.RegisterRequest;
import com.aitsaid.authservice.entities.TokenBlockList;
import com.aitsaid.authservice.entities.User;
import com.aitsaid.authservice.exceptions.*;
import com.aitsaid.authservice.mappers.UserMapper;
import com.aitsaid.authservice.repositories.TokenBlockListRepository;
import com.aitsaid.authservice.repositories.UserRepository;
import com.aitsaid.authservice.security.JwtUtil;

import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author radouane
 **/
@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final TokenBlockListRepository tokenBlockListRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountRestClient accountRestClient;

    public AuthService(UserRepository userRepository,
                       TokenBlockListRepository tokenBlockListRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       AccountRestClient accountRestClient) {
        this.userRepository = userRepository;
        this.tokenBlockListRepository = tokenBlockListRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.accountRestClient = accountRestClient;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            System.out.println("Email already exists: " + request.getEmail());
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (request.getCin() != null) {
            if (userRepository.findByCin(request.getCin()).isPresent()) {
                throw new CinAlreadyExistsException(request.getCin());
            }
        }
        
        User user = UserMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String accountType = request.getType();
        if(accountType == null){
            accountType = "CURRENT_ACCOUNT";
        }

        User savedUser = userRepository.save(user);

        BankAccountRequestDTO accountDTO = new BankAccountRequestDTO();
        accountDTO.setUserId(savedUser.getId());
        accountDTO.setType(accountType);
        
        if ("CURRENT_ACCOUNT".equals(accountType)) {
            accountDTO.setOverDraft(1000.0); // Default overdraft
        } else if ("SAVING_ACCOUNT".equals(accountType)) {
            accountDTO.setInterestRate(3.5); // Default interest rate
        }
        
        // Generate 16 digit RIB
        StringBuilder ribBuilder = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 16; i++) {
            ribBuilder.append(random.nextInt(10));
        }
        accountDTO.setIban(ribBuilder.toString());
        
        accountDTO.setExpirationDate(LocalDate.now().plusYears(4));
        
        accountRestClient.createAccount(accountDTO);

        return new RegisterResponse(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName(),  "Registration successful");
    }

    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);

            return new LoginResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), user.getId(), user.getAddress(), user.getPhoneNumber(), user.getCin(), "Login successful");

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or pasword");
        } catch (Exception e) {
            throw new RuntimeException("Login failed " + e.getMessage());
        }
    }

    public void logout(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new InvalidTokenException("Authorization token is required");
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token.isEmpty()) {
                throw new InvalidTokenException("Token cannot be empty");
            }

            if (!jwtUtil.isTokenValid(token)) {
                throw new InvalidTokenException("Invalid or expired token");
            }

            if (isTokenBlocked(token)) {
                throw new InvalidTokenException("Token is already invalidated");
            }

            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            TokenBlockList blockedToken = new TokenBlockList();
            blockedToken.setToken(token);
            blockedToken.setUser(user);

            tokenBlockListRepository.save(blockedToken);

            SecurityContextHolder.clearContext();

        } catch (InvalidTokenException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new LogoutFailedException(e);
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        }
        throw new UserNotAuthenticatedException();
    }

    public boolean isTokenBlocked(String token) {
        return tokenBlockListRepository.existsByToken(token);
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token.isEmpty()) {
                return false;
            }

            // Vérifier si le token est valide
            if (!jwtUtil.isTokenValid(token)) {
                return false;
            }

            // Vérifier si le token est bloqué
            if (isTokenBlocked(token)) {
                return false;
            }

            // Vérifier si l'utilisateur existe
            String username = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(username).isPresent();

        } catch (Exception e) {
            return false;
        }
    }
}
