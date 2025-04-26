package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String text;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
}
