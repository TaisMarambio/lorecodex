package com.lorecodex.backend.dto.response.igdb;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Data
@Getter
@Setter
public class CreateGameFromIgdbRequest {
    private Long igdbId;
    private String title;
    private String coverImage;
    private Set<String> genres;
    private String description;
    private LocalDate releaseDate;
}
