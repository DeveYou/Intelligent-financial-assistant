package com.khaoula.recipientservice.controller;

import com.khaoula.recipientservice.dto.RecipientRequest;
import com.khaoula.recipientservice.dto.RecipientResponse;
import com.khaoula.recipientservice.exception.ResourceNotFoundException;
import com.khaoula.recipientservice.model.ApiResponse;
import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.repository.RecipientRepository;
import com.khaoula.recipientservice.service.RecipientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;
    private final RecipientRepository  recipientRepository;

    public RecipientController(RecipientService recipientService, RecipientRepository recipientRepository) {
        this.recipientService = recipientService;
        this.recipientRepository = recipientRepository;
    }

    /**
     * Extrait le userId de l'authentification Spring Security
     * Le JWT doit contenir le userId dans le sujet (subject) ou dans les claims
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        // Si vous stockez le userId dans le subject du JWT
        String userIdStr = authentication.getName(); // Par défaut, getName() retourne le subject

        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new SecurityException("Invalid user ID in token: " + userIdStr);
        }

        // Alternative: Si vous stockez le userId dans les claims
        // Jwt jwt = (Jwt) authentication.getPrincipal();
        // return Long.parseLong(jwt.getClaimAsString("userId"));
    }

    private RecipientResponse toResponse(Recipient r) {
        RecipientResponse resp = new RecipientResponse();
        resp.setId(r.getId());
        resp.setBank(r.getBank());
        resp.setIban(r.getIban());
        resp.setFullName(r.getFullName());
        resp.setCreatedAt(r.getCreatedAt());
        return resp;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<RecipientResponse>> getRecipientList() {
        Long userId = getCurrentUserId();
        List<Recipient> recipients = recipientService.getRecipientList(userId);
        List<RecipientResponse> responses = recipients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecipientResponse> addRecipient(
            @Valid @RequestBody RecipientRequest request) {
        Long userId = getCurrentUserId();

        Recipient recipient = new Recipient();
        recipient.setFullName(request.getFullName());
        recipient.setIban(request.getIban());
        recipient.setBank(request.getBank());

        Recipient saved = recipientService.addRecipient(recipient, userId);
        return ApiResponse.success(toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecipientResponse> updateRecipient(
            @PathVariable Long id,
            @Valid @RequestBody RecipientRequest request) {
        Long userId = getCurrentUserId();

        Recipient recipient = new Recipient();
        recipient.setFullName(request.getFullName());
        recipient.setIban(request.getIban());
        recipient.setBank(request.getBank());

        Recipient updated = recipientService.updateRecipient(id, recipient, userId);
        return ApiResponse.success(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> deleteRecipient(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        recipientService.deleteRecipient(id, userId);
        return ApiResponse.success("Recipient deleted successfully");
    }

    @GetMapping("/iban/{iban}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecipientResponse> getByIban(@PathVariable String iban) {
        // Note: Cette méthode peut nécessiter une vérification supplémentaire
        // pour s'assurer que l'utilisateur a accès à ce bénéficiaire
        Recipient recipient = recipientService.getByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with IBAN: " + iban));

        // Vérifier que le bénéficiaire appartient à l'utilisateur
        Long userId = getCurrentUserId();
        if (!recipient.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Recipient not found with IBAN: " + iban);
        }

        return ApiResponse.success(toResponse(recipient));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecipientResponse> getRecipientById(@PathVariable Long id) {
        Long userId = getCurrentUserId();

        // Récupérer le bénéficiaire
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with id: " + id));

        // Vérifier que le bénéficiaire appartient à l'utilisateur
        if (!recipient.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Recipient not found with id: " + id);
        }

        return ApiResponse.success(toResponse(recipient));
    }
}