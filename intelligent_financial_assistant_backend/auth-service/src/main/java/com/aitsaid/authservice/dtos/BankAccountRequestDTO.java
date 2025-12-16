package com.aitsaid.authservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountRequestDTO {
     private String iban;
    private Double balance;
    private String type; // Using String to avoid Enum dependency issues, or I can replicate the Enum
    private Double overDraft;
    private Double interestRate;
    private Long userId;
    private LocalDate expirationDate;
}
