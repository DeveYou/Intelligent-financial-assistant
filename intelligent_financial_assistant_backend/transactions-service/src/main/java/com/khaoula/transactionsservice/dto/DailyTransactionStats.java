package com.khaoula.transactionsservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTransactionStats {
    private Date date;
    private Long count;
}
