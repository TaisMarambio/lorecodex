package com.lorecodex.backend.dto.response.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
public class IgdbGameResponse {
    @JsonProperty("id")
    private Long igdbId;

    private String name; // título real
    private String summary; // lo usás como description
    private Double rating;
    private CoverResponse cover;
    private Set<GenreResponse> genres;

    @JsonProperty("release_dates")
    private List<ReleaseDateResponse> releaseDates;

    @JsonProperty("involved_companies")
    private List<InvolvedCompanyResponse> involvedCompanies;
}


