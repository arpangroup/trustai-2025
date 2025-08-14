package com.trustai.common_base.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class IncomeSummaryDto {
    private String type;
    private BigDecimal dailyIncome;
    private BigDecimal totalIncome;
}