package com.lorecodex.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRequest {
    @NotBlank(message = "Challenge title is required")
    private String title;

    private String description;

    @NotNull(message = "Game ID is required")
    private Long gameId;
}
