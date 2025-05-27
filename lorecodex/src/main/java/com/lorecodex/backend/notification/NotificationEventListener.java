package com.lorecodex.backend.notification;

import com.lorecodex.backend.notification.event.FollowedUserEvent;
import com.lorecodex.backend.notification.event.GuideCommentedEvent;
import com.lorecodex.backend.notification.event.NewsCommentedEvent;
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

    @EventListener
    public void handleGuideCommented(GuideCommentedEvent event) {
        String message = "¡" + event.commenterUsername() + " comentó tu guía \"" + event.guideTitle() + "\"!";
        notificationService.notifyUser(event.authorId(), message);
    }

    @EventListener
    public void handleNewsCommented(NewsCommentedEvent event) {
        String message = "¡" + event.commenterUsername() + " comentó tu noticia \"" + event.newsTitle() + "\"!";
        notificationService.notifyUser(event.authorId(), message);
    }

}