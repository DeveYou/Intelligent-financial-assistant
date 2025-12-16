package com.lachguer.accountservice.dto;

import com.lachguer.accountservice.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDistributionDTO {
    private AccountType type;
    private Long count;
}
