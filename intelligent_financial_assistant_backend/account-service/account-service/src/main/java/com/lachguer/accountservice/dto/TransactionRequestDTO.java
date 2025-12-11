package com.lachguer.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private Long accountId;
    private Long userId;
    private BigDecimal amount;
    private String type; // DEBIT or CREDIT
    private String description;
}
