package com.lorecodex.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDifficultyRequest {
    @NotNull(message = "Difficulty level is required")
    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 6, message = "Difficulty cannot exceed 6")
    private Integer difficultyLevel;
}