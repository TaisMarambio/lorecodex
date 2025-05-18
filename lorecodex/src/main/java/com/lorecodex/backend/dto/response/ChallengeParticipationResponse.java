package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipationResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long challengeId;
    private String challengeTitle;
    private LocalDateTime joinedAt;
    private Boolean completed;
    private LocalDateTime completedAt;
}