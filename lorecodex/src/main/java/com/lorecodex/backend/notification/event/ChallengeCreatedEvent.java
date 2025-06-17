package com.lorecodex.backend.notification.event;

public record ChallengeCreatedEvent(Long authorId, String authorUsername, String challengeTitle) {
}
