package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.dto.response.GameSearchResponse;
import com.lorecodex.backend.dto.response.igdb.GenreResponse;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.dto.response.igdb.ReleaseDateResponse;
import com.lorecodex.backend.util.PlayerCountFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IgdbGameMapper {

    private final PlayerCountFormatter playerCountFormatter;

    public GameSearchResponse toSearchDto(IgdbGameResponse igdbGame) {
        return GameSearchResponse.builder()
                .igdbId(igdbGame.getIgdbId())
                .title(igdbGame.getName())
                .coverImage(upgradeCoverImage(igdbGame.getCover() != null ? igdbGame.getCover().getUrl() : null))
                .releaseDate(getFirstReleaseDate(igdbGame))
                .description(igdbGame.getSummary())
                .rating(igdbGame.getRating())
                .genres(igdbGame.getGenres() != null
                        ? igdbGame.getGenres().stream()
                        .map(GenreResponse::getName)
                        .collect(Collectors.toSet())
                        : Set.of())
                .playerCount(playerCountFormatter.format(null))
                .tags(extractKeywords(igdbGame))
                .build();
    }

    public GameDetailResponse toDetailDto(IgdbGameResponse igdbGame) {
        return GameDetailResponse.builder()
                .igdbId(igdbGame.getIgdbId())
                .title(igdbGame.getName())
                .description(igdbGame.getSummary())
                .rating(igdbGame.getRating())
                .coverImage(upgradeCoverImage(igdbGame.getCover() != null ? igdbGame.getCover().getUrl() : null))
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
                .playerCount(playerCountFormatter.format(null))
                .tags(extractKeywords(igdbGame))
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

    private String upgradeCoverImage(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String safeUrl = path.startsWith("http") ? path : "https:" + path;
        return safeUrl.replace("t_thumb", "t_cover_big");
    }

    private Set<String> extractKeywords(IgdbGameResponse igdbGame) {
        if (igdbGame.getKeywords() == null) {
            return Set.of();
        }

        return igdbGame.getKeywords().stream()
                .map(keyword -> keyword.getName())
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toSet());
    }

}
