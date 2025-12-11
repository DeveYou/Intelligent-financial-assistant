package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import lombok.Data;

import java.util.Date;

@Data
public class BankAccountResponseDTO {
    private Long id;
    private String rib;
    private Double balance;
    private Boolean isActive;
    private Date createdAt;
    private AccountType type;
    private Double overDraft;
    private Double interestRate;
    private Long userId;
}

