package com.khaoula.transactionsservice.dto;

import com.khaoula.transactionsservice.domain.TransactionStatus;
import com.khaoula.transactionsservice.domain.TransactionType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author radouane
 **/
@Data
public class TransactionFilterDTO {
    private Long userId;
    private Long bankAccountId;
    private TransactionType type;
    private TransactionStatus status;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    // Pagination
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "date";
    private String sortDirection = "DESC";
}
