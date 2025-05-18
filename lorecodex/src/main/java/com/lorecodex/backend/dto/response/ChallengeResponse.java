package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private String creatorUsername;
    private Long gameId;
    private String gameTitle;
    private String gameCoverImage;
    private LocalDateTime createdAt;
    private Integer participantCount;
    private Integer completionCount;
    private Double averageDifficulty;
    private Boolean userParticipating;
    private Boolean userCompleted;
    private Integer userDifficultyRating;
}