package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;

public class AccountDistributionDTO {
    private AccountType type;
    private Long count;

    public AccountDistributionDTO() {
    }

    public AccountDistributionDTO(AccountType type, Long count) {
        this.type = type;
        this.count = count;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
