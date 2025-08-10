package com.trustai.common_base.event;

import com.trustai.common_base.enums.TriggerType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisteredEvent {
    private Long refereeId;
    private Long referrerId;
    private TriggerType triggerType;

    /*public UserRegisteredEvent(Long refereeId, Long referrerId, TriggerType triggerType) {
        this.refereeId = refereeId;
        this.referrerId = referrerId;
        this.triggerType = triggerType;
    }*/

    public UserRegisteredEvent(Long refereeId, Long referrerId) {
        this.refereeId = refereeId;
        this.referrerId = referrerId;
    }
}
