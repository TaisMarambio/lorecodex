package com.lorecodex.backend.dto.response;

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
    private Set<String> genres;
}
