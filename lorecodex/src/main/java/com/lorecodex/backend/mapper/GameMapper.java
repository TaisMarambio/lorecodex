package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.util.PlayerCountFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private final PlayerCountFormatter playerCountFormatter;

    public GameDetailResponse toDTO(Game game) {
        if (game == null) {
            return null;
        }

        return GameDetailResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .coverImage(upgradeCoverImage(game.getCoverImage()))
                .releaseDate(game.getReleaseDate())
                .releaseYear(game.getReleaseYear())
                .releaseDateUnknown(Boolean.TRUE.equals(game.getReleaseDateUnknown()))
                .createdAt(game.getCreatedAt())
                .rating(game.getRating())
                .likes(game.getLikes())
                .genres(game.getGenres())
                .developersAndPublishers(game.getDevelopersAndPublishers())
                .tags(game.getTags())
                .playerCount(playerCountFormatter.format(game.getLikes()))
                .build();
    }

    public List<GameDetailResponse> toDTOList(List<Game> games) {
        if (games == null) {
            return List.of();
        }

        return games.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Game toEntity(GameRequest gameRequest) {
        if (gameRequest == null) {
            return null;
        }

        Game game = new Game();
        game.setTitle(gameRequest.getTitle());
        game.setDescription(gameRequest.getDescription());
        game.setCoverImage(gameRequest.getCoverImage());
        game.setReleaseDate(gameRequest.getReleaseDate());
        game.setReleaseYear(gameRequest.getReleaseYear());
        game.setReleaseDateUnknown(Boolean.TRUE.equals(gameRequest.getReleaseDateUnknown()));
        normalizeReleaseFields(game);
        game.setGenres(copyOrEmpty(gameRequest.getGenres()));
        game.setDevelopersAndPublishers(copyOrEmpty(gameRequest.getDevelopersAndPublishers()));
        game.setTags(copyOrEmpty(gameRequest.getTags()));
        game.setRating(0.0);
        game.setLikes(0);

        return game;
    }

    public void updateEntityFromRequest(Game game, GameRequest gameRequest) {
        if (gameRequest.getTitle() != null) {
            game.setTitle(gameRequest.getTitle());
        }
        if (gameRequest.getDescription() != null) {
            game.setDescription(gameRequest.getDescription());
        }
        if (gameRequest.getCoverImage() != null) {
            game.setCoverImage(gameRequest.getCoverImage());
        }
        if (gameRequest.getReleaseDate() != null) {
            game.setReleaseDate(gameRequest.getReleaseDate());
        }
        if (gameRequest.getReleaseYear() != null) {
            game.setReleaseYear(gameRequest.getReleaseYear());
        }
        if (gameRequest.getReleaseDateUnknown() != null) {
            game.setReleaseDateUnknown(Boolean.TRUE.equals(gameRequest.getReleaseDateUnknown()));
        }
        normalizeReleaseFields(game);
        if (gameRequest.getGenres() != null) {
            game.setGenres(gameRequest.getGenres());
        }
        if (gameRequest.getDevelopersAndPublishers() != null) {
            game.setDevelopersAndPublishers(copyOrEmpty(gameRequest.getDevelopersAndPublishers()));
        }
        if (gameRequest.getTags() != null) {
            game.setTags(copyOrEmpty(gameRequest.getTags()));
        }
    }

    private void normalizeReleaseFields(Game game) {
        // Regla de negocio:
        // - Si releaseDateUnknown == true => limpiar releaseDate y releaseYear
        // - Si releaseDate tiene valor => releaseDateUnknown = false
        // - Si solo hay releaseYear => releaseDate = null y releaseDateUnknown = false
        if (Boolean.TRUE.equals(game.getReleaseDateUnknown())) {
            game.setReleaseDate(null);
            game.setReleaseYear(null);
            return;
        }
        if (game.getReleaseDate() != null) {
            game.setReleaseDateUnknown(false);
            return;
        }
        if (game.getReleaseYear() != null) {
            game.setReleaseDate(null);
            game.setReleaseDateUnknown(false);
        }
    }

    private String upgradeCoverImage(String url) {
        if (url == null) {
            return null;
        }
        if (url.contains("t_thumb")) {
            return url.replace("t_thumb", "t_cover_big");
        }
        return url;
    }

    private Set<String> copyOrEmpty(Set<String> source) {
        if (source == null) {
            return new HashSet<>();
        }
        return new HashSet<>(source);
    }
}
