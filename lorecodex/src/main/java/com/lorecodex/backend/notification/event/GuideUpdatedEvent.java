package com.lorecodex.backend.notification.event;

public record GuideUpdatedEvent(Long authorId, String authorUsername, String guideTitle) {
}
