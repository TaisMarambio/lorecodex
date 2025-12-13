package com.lorecodex.backend.notification.event;

public record ChallengeCommentedEvent(Long challengeOwnerId, String commenterUsername, String challengeTitle) {
}