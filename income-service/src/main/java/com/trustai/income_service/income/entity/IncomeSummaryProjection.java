package com.trustai.income_service.income.entity;

import com.trustai.common_base.enums.IncomeType;

import java.math.BigDecimal;

public interface IncomeSummaryProjection {
    IncomeType getIncomeType();
    BigDecimal getTodayAmount();
    BigDecimal getYesterdayAmount();
    BigDecimal getLast7DaysAmount();
    BigDecimal getTotalAmount();
}
