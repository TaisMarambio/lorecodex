package com.lorecodex.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailResponse {
    private Long id;
    private Long igdbId;
    private String title;
    private String description;
    private String coverImage;
    private LocalDate releaseDate;
    private Integer releaseYear; // nuevo campo: solo año
    private Boolean releaseDateUnknown; // nuevo campo: estado desconocido
    @JsonProperty("averageRating")
    private Double rating;
    private Integer likes;
    private Set<String> genres;
    private Set<String> developersAndPublishers;
    private Set<String> tags;
    private String playerCount;
    private Instant createdAt;
}
