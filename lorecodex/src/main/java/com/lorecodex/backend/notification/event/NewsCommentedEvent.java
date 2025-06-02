package com.lorecodex.backend.notification.event;

public record NewsCommentedEvent(Long authorId, String commenterUsername, String newsTitle) {

}