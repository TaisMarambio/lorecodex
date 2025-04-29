package com.lorecodex.backend.dto.request;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private String content;
    private Double rating;
    private Long gameId;
}