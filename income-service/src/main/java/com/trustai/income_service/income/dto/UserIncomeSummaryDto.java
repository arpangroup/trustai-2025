package com.trustai.income_service.income.dto;

import java.math.BigDecimal;

public record UserIncomeSummaryDto (
        String type,
        BigDecimal dailyIncome,
        BigDecimal totalIncome
){}