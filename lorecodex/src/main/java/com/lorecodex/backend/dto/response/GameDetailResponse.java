package com.lorecodex.backend.dto.response;

import lombok.*;
import java.time.LocalDate;
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
    private Integer likes;
    private Set<String> genres;
    private Set<String> developersAndPublishers;
    private Double averageRating;
}