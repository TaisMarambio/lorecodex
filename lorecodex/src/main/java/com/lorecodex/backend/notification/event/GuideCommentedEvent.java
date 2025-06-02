package com.lorecodex.backend.notification.event;

public record GuideCommentedEvent(Long authorId, String commenterUsername, String guideTitle) {

}
