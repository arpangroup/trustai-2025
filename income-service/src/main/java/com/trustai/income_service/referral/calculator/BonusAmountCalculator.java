package com.trustai.income_service.referral.calculator;


import com.trustai.common_base.dto.UserInfo;
import com.trustai.common_base.enums.CalculationType;

import java.math.BigDecimal;

public interface BonusAmountCalculator {
    BigDecimal calculate(UserInfo referrer, UserInfo referee);
    CalculationType getType();
}
