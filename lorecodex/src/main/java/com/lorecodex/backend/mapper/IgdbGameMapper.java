package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.dto.response.GameSearchResponse;
import com.lorecodex.backend.dto.response.igdb.GenreResponse;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.dto.response.igdb.ReleaseDateResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IgdbGameMapper {

    public GameSearchResponse toSearchDto(IgdbGameResponse igdbGame) {
        return GameSearchResponse.builder()
                .igdbId(igdbGame.getIgdbId())
                .title(igdbGame.getName())
                .coverImage(
                        igdbGame.getCover() != null
                                ? "https:" + igdbGame.getCover().getUrl()
                                : null)
                .releaseDate(getFirstReleaseDate(igdbGame))
                .genres(igdbGame.getGenres() != null
                        ? igdbGame.getGenres().stream()
                        .map(GenreResponse::getName)
                        .collect(Collectors.toSet())
                        : Set.of())
                .build();
    }

    public GameDetailResponse toDetailDto(IgdbGameResponse igdbGame) {
        return GameDetailResponse.builder()
                .igdbId(igdbGame.getIgdbId())
                .title(igdbGame.getName())
                .description(igdbGame.getSummary())
                .averageRating(igdbGame.getRating())
                .coverImage(
                        igdbGame.getCover() != null
                                ? "https:" + igdbGame.getCover().getUrl()
                                : null
                )
                .releaseDate(getFirstReleaseDate(igdbGame))
                .genres(igdbGame.getGenres() != null
                        ? igdbGame.getGenres().stream()
                        .map(GenreResponse::getName)
                        .collect(Collectors.toSet())
                        : Set.of())
                .developersAndPublishers(igdbGame.getInvolvedCompanies() != null
                        ? igdbGame.getInvolvedCompanies().stream()
                        .map(ic -> ic.getCompany().getName())
                        .collect(Collectors.toSet())
                        : Set.of())
                .averageRating(null)
                .likes(null)
                .build();
    }

    public LocalDate getFirstReleaseDate(IgdbGameResponse game) {
        if (game.getReleaseDates() == null) {
            return null;
        }

        return game.getReleaseDates().stream()
                .map(ReleaseDateResponse::getDate)
                .filter(Objects::nonNull) // filtra fechas nulas
                .map(timestamp -> Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .findFirst()
                .orElse(null);
    }

}
