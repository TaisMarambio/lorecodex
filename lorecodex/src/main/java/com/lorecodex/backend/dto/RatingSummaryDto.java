package com.lorecodex.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RatingSummaryDto {
    private Double average;
    private Integer mine;
}
