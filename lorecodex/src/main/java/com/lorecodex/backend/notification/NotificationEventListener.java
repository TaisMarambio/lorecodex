package com.lorecodex.backend.notification;

import com.lorecodex.backend.notification.event.FollowedUserEvent;
import com.lorecodex.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleFollowedUser(FollowedUserEvent event) {
        String message = "¡" + event.followerUsername() + " comenzó a seguirte!";
        notificationService.notifyUser(event.followedUserId(), message);
    }
}