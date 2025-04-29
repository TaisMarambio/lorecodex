package com.lorecodex.backend.dto.request;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
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
    private String genre;
    private String description;
    private String coverImage;
    private LocalDate releaseDate;
    private Set<String> genres;
    private Set<String> awards;
}
