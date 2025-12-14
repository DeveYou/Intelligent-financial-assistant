package com.khaoula.recipientservice.controller;

import com.khaoula.recipientservice.client.UserClient;
import com.khaoula.recipientservice.client.UserClient.UserResponse;
import com.khaoula.recipientservice.dto.RecipientResponse;
import com.khaoula.recipientservice.model.ApiResponse;
import com.khaoula.recipientservice.model.Recipient;
import com.khaoula.recipientservice.dto.RecipientRequest;
import com.khaoula.recipientservice.service.RecipientService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;
    private final UserClient userClient;

    public RecipientController(RecipientService recipientService, UserClient userClient) {
        this.recipientService = recipientService;
        this.userClient = userClient;
    }

    // Résout le userId soit à partir du header 'user-id' (tests / simplicité) soit via le header Authorization (Feign)
    private Long resolveUserId(Long userIdHeader, String authorizationHeader) {
        if (userIdHeader != null) return userIdHeader;
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new IllegalArgumentException("Authorization header or user-id header is required");
        }
        UserResponse response = userClient.validateToken(authorizationHeader);
        if (response == null || !response.isValid() || response.getUserId() == null) {
            throw new RuntimeException("Unable to resolve user from token");
        }
        return response.getUserId();
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
    public ApiResponse<List<RecipientResponse>> getRecipientList(@RequestHeader(value = "user-id", required = false) Long userId,
                                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long resolvedUserId = resolveUserId(userId, authorization);
        List<Recipient> recipients = recipientService.getRecipientList(resolvedUserId);
        List<RecipientResponse> responses = recipients.stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @PostMapping
    public ApiResponse<RecipientResponse> addRecipient(@Valid @RequestBody RecipientRequest request,
                                                       @RequestHeader(value = "user-id", required = false) Long userId,
                                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long resolvedUserId = resolveUserId(userId, authorization);
        Recipient recipient = new Recipient();
        recipient.setFullName(request.getFullName());
        recipient.setIban(request.getIban());
        recipient.setBank(request.getBank());
        Recipient saved = recipientService.addRecipient(recipient, resolvedUserId);
        return ApiResponse.success(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<RecipientResponse> updateRecipient(@PathVariable Long id, @Valid @RequestBody RecipientRequest request,
                                                          @RequestHeader(value = "user-id", required = false) Long userId,
                                                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long resolvedUserId = resolveUserId(userId, authorization);
        Recipient recipient = new Recipient();
        recipient.setFullName(request.getFullName());
        recipient.setIban(request.getIban());
        recipient.setBank(request.getBank());
        Recipient updated = recipientService.updateRecipient(id, recipient, resolvedUserId);
        return ApiResponse.success(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRecipient(@PathVariable Long id,
                                               @RequestHeader(value = "user-id", required = false) Long userId,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        Long resolvedUserId = resolveUserId(userId, authorization);
        recipientService.deleteRecipient(id, resolvedUserId);
        return ApiResponse.success("Recipient deleted successfully");
    }

    @GetMapping("/iban/{iban}")
    public ApiResponse<RecipientResponse> getByIban(@PathVariable String iban) {
        Recipient recipient = recipientService.getByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with IBAN: " + iban));
        return ApiResponse.success(toResponse(recipient));
    }
}
