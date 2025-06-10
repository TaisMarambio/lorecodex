package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void notifyUser(Long recipientId, String message);
    List<NotificationResponse> getNotifications(Long userId);
    void markAsRead(Long notificationId);
    List<Long> getFollowersOfUser(Long userId);
    void markAllAsRead(Long userId);

}

