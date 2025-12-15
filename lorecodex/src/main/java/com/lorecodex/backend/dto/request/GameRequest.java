package com.lorecodex.backend.dto.request;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {
    private String title;
    private Double rating;
    private String genre;  // Campo singular para compatibilidad
    private String description;
    private String coverImage;
    private LocalDate releaseDate;
    private Set<String> genres;  // Campo plural
    private Set<String> tags;
    private Set<String> developersAndPublishers;
}
