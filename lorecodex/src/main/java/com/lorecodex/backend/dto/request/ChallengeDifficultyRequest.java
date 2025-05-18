package com.lorecodex.backend.dto.request;

import lombok.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDifficultyRequest {
    @NotNull(message = "Difficulty level is required")
    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 6, message = "Difficulty must be at most 6")
    private Integer difficultyLevel;
}