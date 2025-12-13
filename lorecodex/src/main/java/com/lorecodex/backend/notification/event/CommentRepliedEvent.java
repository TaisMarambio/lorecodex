package com.lorecodex.backend.notification.event;

public record CommentRepliedEvent(
        Long originalCommentOwnerId,
        String replierUsername,
        String contextTitle,  // título de la guía/news/lista/challenge
        String contextType    // "guide", "news", "list", "challenge"
) {
}