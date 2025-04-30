package com.lorecodex.backend.dto.request;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingRequest {
    private Long gameId;
    private Double rating;
}
