package com.trustai.common_base.dto;

import com.trustai.common_base.enums.IncomeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class IncomeSummaryDto {
    private IncomeType incomeType;
    private BigDecimal todayAmount;
    private BigDecimal yesterdayAmount;
    private BigDecimal last7DaysAmount;
    private BigDecimal totalAmount;
}