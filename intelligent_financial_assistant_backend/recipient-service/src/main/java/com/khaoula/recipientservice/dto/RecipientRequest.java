package com.khaoula.recipientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RecipientRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "IBAN is required")
    @Size(min = 15, max = 34, message = "IBAN must be between 15 and 34 characters")
    private String iban;

    // Optional bank name
    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bank;

    public RecipientRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
