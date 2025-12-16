package com.khaoula.transactionsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientRequest {
    private String fullName;
    private String iban;
    private String bank;
}
