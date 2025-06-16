package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeProgressDto {

    private Long   challengeId;
    private double progress;      // 0-100
    private int    completed;     // cuántos
    private int    total;         // total de tasks
    private List<Long> completedItems;
}