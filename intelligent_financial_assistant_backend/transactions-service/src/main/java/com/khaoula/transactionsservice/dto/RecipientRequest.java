package com.khaoula.transactionsservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipientRequest {
    private String fullName;
    private String iban;
    private String bank;

    public RecipientRequest(String fullName, String iban, String bank) {
        this.fullName = fullName;
        this.iban = iban;
        this.bank = bank;
    }
}
