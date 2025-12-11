package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class BankAccountRequestDTO {
    @NotNull(message = "Balance cannot be null")
    private Double balance;
    @NotNull(message = "Type cannot be null")
    private AccountType type;
    private Double overDraft;
    private Double interestRate;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
