package com.lorecodex.backend.notification.event;

public record GuideCreatedEvent(Long authorId, String authorUsername, String guideTitle) {}
