package com.lorecodex.backend.notification;

import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.notification.event.*;
import com.lorecodex.backend.service.FollowService;
import com.lorecodex.backend.service.NotificationService;
import com.lorecodex.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final FollowService followService;
    private final UserService userService;

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

    @EventListener
    public void handleGuideCreatedByFollowedUser(GuidePublishedEvent event) {
        // los seguidores del autor
        List<UserResponse> followers = followService.getFollowers(event.authorId());
        String message = "¡" + event.authorUsername() + " ha publicado una nueva guía: \"" + event.guideTitle() + "\"!";
        for (UserResponse follower : followers) {
            notificationService.notifyUser(follower.getId(), message);
        }
    }

    @EventListener
    public void handleGuideUpdatedByFollowedUser(GuideUpdatedEvent event) {
        // los seguidores del autor
        List<UserResponse> followers = followService.getFollowers(event.authorId());
        String message = "¡" + event.authorUsername() + " ha actualizado la guía: \"" + event.guideTitle() + "\"!";
        for (UserResponse follower : followers) {
            notificationService.notifyUser(follower.getId(), message);
        }
    }

    @EventListener
    public void handleChallengeCreatedByFollowedUser(ChallengeCreatedEvent event) {
        // los seguidores del autor
        List<UserResponse> followers = followService.getFollowers(event.authorId());
        String message = "¡" + event.authorUsername() + " ha creado un nuevo desafío: " + event.challengeTitle() + "!";
        for (UserResponse follower : followers) {
            notificationService.notifyUser(follower.getId(), message);
        }
    }

    @EventListener
    public void handleListCreatedEventByFollowedUser(ListPublishedEvent event) {
        // los seguidores del autor
        List<UserResponse> followers = followService.getFollowers(event.authorId());
        String message = "¡" + event.authorUsername() + " ha creado una nueva lista: " + event.listTitle() + "!";
        for (UserResponse follower : followers) {
            notificationService.notifyUser(follower.getId(), message);
        }
    }

}