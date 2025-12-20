package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class BankAccountResponseDTO {
    private Long id;
    private String iban;
    private Double balance;
    private LocalDate expirationDate;
    private Boolean isActive;
    private Boolean isPaymentByCard;
    private Boolean isWithdrawal;
    private Boolean isOnlinePayment;
    private Boolean isContactless;
    private Date createdAt;
    private AccountType type;
    private Double overDraft;
    private Double interestRate;
    private Long userId;
    private String fullName;
}

