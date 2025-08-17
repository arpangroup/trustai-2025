package com.trustai.notification_service.notification.sender;

import com.trustai.common_base.dto.NotificationRequest;
import com.trustai.common_base.enums.NotificationChannel;

public interface NotificationSender {
    NotificationChannel getChannel(); // Returns the type it supports
    void send(NotificationRequest request);
}
