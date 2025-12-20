package com.lorecodex.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSearchResponse {
    private Long igdbId;
    private String title;
    private String coverImage;
    private LocalDate releaseDate;
    private Integer releaseYear;
    private Boolean releaseDateUnknown;
    private String description;
    private Set<String> genres;
    private Set<String> tags;
    private String playerCount;
    @JsonProperty("averageRating")
    private Double rating;
}
