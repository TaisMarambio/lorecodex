package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameDTO;
import com.lorecodex.backend.model.Game;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GameMapper {

    public GameDTO toDTO(Game game) {
        if (game == null) {
            return null;
        }

        return GameDTO.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .coverImage(game.getCoverImage())
                .releaseDate(game.getReleaseDate())
                .rating(game.getRating())
                .likes(game.getLikes())
                .genres(game.getGenres())
                .awards(game.getAwards())
                .build();
    }

    public List<GameDTO> toDTOList(List<Game> games) {
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
        game.setGenres((Set<String>) gameRequest.getGenres());
        game.setAwards((Set<String>) gameRequest.getAwards());
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
            game.setGenres((Set<String>) gameRequest.getGenres());
        }
        if (gameRequest.getAwards() != null) {
            game.setAwards((Set<String>) gameRequest.getAwards());
        }
    }
}