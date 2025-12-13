package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class BankAccountRequestDTO {
    private String iban;
    private Double balance;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private LocalDate expirationDate;
    @NotNull(message = "Type cannot be null")
    private AccountType type;
    private Double overDraft;
    private Double interestRate;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
