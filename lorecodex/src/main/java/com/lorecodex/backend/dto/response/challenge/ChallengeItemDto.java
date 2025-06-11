package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeItemDto {
    private Long id;
    private String description;
    private Integer order;
}
