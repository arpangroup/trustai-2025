package com.trustai.income_service.income.entity;

import java.math.BigDecimal;

public interface IncomeStatsProjection {
    IncomeHistory.IncomeType getType();
    BigDecimal getTotalIncome();
    BigDecimal getTodayIncome();
    BigDecimal getLast7DaysIncome();
}
