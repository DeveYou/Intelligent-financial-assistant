package com.khaoula.transactionsservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * @author radouane
 **/
@Data
public class TransactionResponseDTO {
    private Long id;
    private Long userId;
    private Long bankAccountId;
    private String reference;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private Long recipientId;
    private String recipientName; // Enrichi depuis recipient-service
    private String recipientIban;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime date;
}
