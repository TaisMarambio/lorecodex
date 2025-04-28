package com.lorecodex.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {
    private String title;
    private Double rating;
    private String genre;
    private String description;
}
