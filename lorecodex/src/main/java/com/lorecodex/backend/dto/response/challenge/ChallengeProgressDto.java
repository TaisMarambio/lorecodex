package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeProgressDto {
    //cuando se completa un item, se actualiza el progreso del challenge
    private Long challengeId;
    private double progress; // percentage 0‑100
    private int completed;
    private int total;
}