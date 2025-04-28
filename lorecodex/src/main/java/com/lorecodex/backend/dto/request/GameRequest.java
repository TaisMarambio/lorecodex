package com.lorecodex.backend.dto.request;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {
    private String title;
    private String description;
    private String coverImage;
    private LocalDate releaseDate;
    private Set<String> genres;
    private Set<String> awards;
}
