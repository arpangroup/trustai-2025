package com.trustai.notification_service.notification.sender;

import com.trustai.notification_service.notification.dto.NotificationRequest;
import com.trustai.notification_service.notification.enums.NotificationChannel;

public interface NotificationSender {
    NotificationChannel getChannel(); // Returns the type it supports
    void send(NotificationRequest request);
}
