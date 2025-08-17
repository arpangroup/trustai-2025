package com.trustai.common_base.event;

import com.trustai.common_base.dto.NotificationRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent  extends ApplicationEvent {
    private final NotificationRequest request;

    public NotificationEvent(Object source, NotificationRequest request) {
        super(source);
        this.request = request;
    }

}
