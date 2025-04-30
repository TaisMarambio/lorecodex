package com.lorecodex.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingResponse {
    private Long id;
    private Long userId;
    private Long gameId;
    private Double rating;
}