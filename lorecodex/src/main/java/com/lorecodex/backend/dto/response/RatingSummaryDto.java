package com.lorecodex.backend.dto.response;

import lombok.*;

/**
 * DTO que combina el promedio de ratings y el rating del usuario actual
 */
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryDto {
    /**
     * Promedio de todos los ratings del juego
     */
    private Double average;

    /**
     * Rating del usuario actual (0.0 si no ha votado o no está autenticado)
     */
    private Double mine;
}