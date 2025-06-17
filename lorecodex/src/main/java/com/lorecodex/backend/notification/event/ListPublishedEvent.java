package com.lorecodex.backend.notification.event;

public record ListPublishedEvent(Long authorId, String authorUsername, String listTitle) {
}
