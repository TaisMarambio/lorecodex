package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Notification;

import java.util.List;

public interface NotificationService {
    void notifyUser(Long recipientId, String message);
    List<Notification> getNotifications(Long userId);
    void markAsRead(Long notificationId);
}

