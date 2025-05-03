package com.lorecodex.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private Double rating;
    private Integer likes;
    private Integer dislikes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private Long gameId;
    private String gameTitle;
}