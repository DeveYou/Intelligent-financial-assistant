package com.aitsaid.authservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author radouane
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Requête d'inscription")
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    @Schema(description = "Prénom", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Nom", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Schema(description = "Mot de passe", example = "password123", minLength = 6)
    private String password;

}
