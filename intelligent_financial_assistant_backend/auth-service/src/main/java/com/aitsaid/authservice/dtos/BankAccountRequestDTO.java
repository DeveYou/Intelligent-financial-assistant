package com.aitsaid.authservice.dtos;

import lombok.Data;

@Data
public class BankAccountRequestDTO {
    private String type;
    private Long userId;
}
