package com.lorecodex.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingDTO {
    private Long gameId;
    private Double rating;
}