package com.khaoula.transactionsservice.dto;

import com.khaoula.transactionsservice.domain.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author radouane
 **/
@Data
public class TransactionRequestDTO {
    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;
    private TransactionType type;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
    private BigDecimal amount;
    private String recipientIban;
    private Long recipientId;
    private String recipientName;
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
