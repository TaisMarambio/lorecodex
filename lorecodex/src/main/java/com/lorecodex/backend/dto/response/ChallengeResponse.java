package com.lorecodex.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private String creatorUsername;
    private Long creatorId;
    private String gameName;
    private Long gameId;
    private String gameCoverImage;
    private LocalDateTime createdAt;
    private Integer participantsCount;
    private Integer completedCount;
    private Double averageDifficulty;
    private Boolean userJoined;
    private Boolean userCompleted;
    private Integer userDifficultyRating;
}