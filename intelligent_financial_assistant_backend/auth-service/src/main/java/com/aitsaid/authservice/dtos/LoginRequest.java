package com.aitsaid.authservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author radouane
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de connexion")
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Mot de passe", example = "password123")
    private String password;
}
