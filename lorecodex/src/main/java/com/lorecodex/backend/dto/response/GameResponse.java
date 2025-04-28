package com.lorecodex.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameResponse {
    private Long id;
    private String title;
    private Double rating;
    private String genre;
    private String description;
}
