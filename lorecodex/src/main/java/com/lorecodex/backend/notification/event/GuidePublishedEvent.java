package com.lorecodex.backend.notification.event;

public record GuidePublishedEvent(Long authorId, String authorUsername, String guideTitle) {}
