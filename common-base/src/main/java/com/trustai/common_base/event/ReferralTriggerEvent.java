package com.trustai.common_base.event;

import com.trustai.common_base.enums.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReferralTriggerEvent {
    private final Long userId;             // Referee ID
    private final TriggerType triggerType; // FIRST_DEPOSIT, ACCOUNT_ACTIVATION, etc.
}
