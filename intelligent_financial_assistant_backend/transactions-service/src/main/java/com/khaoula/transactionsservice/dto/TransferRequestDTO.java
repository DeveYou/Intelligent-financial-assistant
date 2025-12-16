package com.khaoula.transactionsservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author radouane
 **/
@Data
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
}
