package com.khaoula.transactionsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTransactionStats {
    private Date date;
    private Long count;
}
