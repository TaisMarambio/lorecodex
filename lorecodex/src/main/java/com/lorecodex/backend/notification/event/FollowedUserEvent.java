package com.lorecodex.backend.notification.event;

public record FollowedUserEvent(Long followedUserId, String followerUsername) {

}