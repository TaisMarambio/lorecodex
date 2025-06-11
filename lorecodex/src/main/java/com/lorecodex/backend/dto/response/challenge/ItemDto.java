package com.lorecodex.backend.dto.response.challenge;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String description;
    private Integer order;
    private Boolean completed; // true si el usuario autenticado lo completó
}
