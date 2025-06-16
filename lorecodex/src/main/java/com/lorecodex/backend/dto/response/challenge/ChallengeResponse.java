package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

import java.util.List;

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
    private String difficulty;
    private String creatorUsername;
    private Long creatorId; // Nuevo campo
    private List<ChallengeItemDto> items;
}