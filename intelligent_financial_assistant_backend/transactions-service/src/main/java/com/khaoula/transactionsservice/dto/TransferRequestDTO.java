package com.khaoula.transactionsservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferRequestDTO {
    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal amount;
    @NotBlank(message = "Recipient IBAN is required for transfer")
    private String recipientIban;
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    public void setRecipientIban(String recipientIban) {
        this.recipientIban = recipientIban;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
