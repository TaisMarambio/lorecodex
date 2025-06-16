package com.lorecodex.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRequest {
    @NotBlank
    private String title;
    @Size(max = 1500)
    private String description;

    // Descripciones de ítems en orden
    private List<@NotBlank String> items;

    @NotBlank
    private String difficulty;
}