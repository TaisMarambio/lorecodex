package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.model.Comment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
