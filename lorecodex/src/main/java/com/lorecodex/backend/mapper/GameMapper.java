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

        // Calcular la cantidad de ratings
        Integer ratingCount = (game.getUserRatings() != null)
                ? game.getUserRatings().size()
                : 0;

        // Calcular el promedio de ratings real
        Double averageRating = 0.0;
        if (game.getUserRatings() != null && !game.getUserRatings().isEmpty()) {
            double sum = game.getUserRatings().stream()
                    .mapToDouble(ur -> ur.getRating())
                    .sum();
            averageRating = sum / game.getUserRatings().size();
        }

        return GameDetailResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .coverImage(upgradeCoverImage(game.getCoverImage()))
                .releaseDate(game.getReleaseDate())
                .rating(averageRating) // Usar el promedio calculado
                .createdAt(game.getCreatedAt())
                .rating(game.getRating())
                .likes(game.getLikes())
                .ratingCount(ratingCount)
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
