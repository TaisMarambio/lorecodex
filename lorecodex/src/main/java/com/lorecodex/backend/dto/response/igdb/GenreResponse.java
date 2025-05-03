package com.lorecodex.backend.dto.response.igdb;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class GenreResponse {
    private Long id;
    private String name;
}
