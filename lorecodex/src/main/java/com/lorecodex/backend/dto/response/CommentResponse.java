package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
}
