package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private String creatorUsername;
    private List<ChallengeItemDto> items;
}
