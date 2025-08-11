package com.trustai.income_service.referral.impl;

import com.trustai.common_base.dto.UserInfo;
import com.trustai.common_base.enums.TriggerType;
import com.trustai.income_service.referral.AbstractReferralBonusStrategy;

import java.math.BigDecimal;

public class ManualApprovalBonusStrategy extends AbstractReferralBonusStrategy {

    @Override
    public boolean isEligible(UserInfo referee) {
//        return referee.isReferralApproved();
        return false;
    }

    @Override
    protected BigDecimal getBonusAmount(UserInfo referrer, UserInfo referee) {
        return calculator.calculate(referrer, referee);
    }

    @Override
    protected TriggerType getTriggerType() {
        return TriggerType.MANUAL_APPROVAL;
    }

}
