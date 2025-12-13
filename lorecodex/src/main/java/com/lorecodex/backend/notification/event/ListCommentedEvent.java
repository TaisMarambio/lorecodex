package com.lorecodex.backend.notification.event;

public record ListCommentedEvent(Long listOwnerId, String commenterUsername, String listTitle) {
}