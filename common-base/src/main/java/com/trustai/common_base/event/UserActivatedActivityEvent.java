package com.trustai.common_base.event;

import lombok.Getter;

@Getter
public class UserActivatedActivityEvent extends UserActivityEvent {

    public UserActivatedActivityEvent(Object source, Long userId) {
        super(source, userId, "ACTIVATED");
    }
}
