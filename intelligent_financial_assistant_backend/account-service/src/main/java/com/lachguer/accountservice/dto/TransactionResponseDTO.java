package com.lachguer.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Long userId;
    private BigDecimal amount;
    private String type; // DEBIT or CREDIT
    private String description;
    private Date createdAt;
}
