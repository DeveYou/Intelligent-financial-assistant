package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotNull;

public class BankAccountRequestDTO {
    @NotNull(message = "Type cannot be null")
    private AccountType type;
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
