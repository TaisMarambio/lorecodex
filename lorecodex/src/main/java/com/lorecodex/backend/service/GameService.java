package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameResponse;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import java.util.List;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();
    Optional<Game> getGameById(Long id);
    Game createGame(Game game);
    Game updateGame(Long id, Game game);
    GameResponse createGame(GameRequest request);
    GameResponse getGame(Long id);
    List<GameResponse> getAllGames();
    GameResponse updateGame(Long id, GameRequest request);
    void deleteGame(Long id);
    Game incrementLikes(Long id);
    Game rateGame(Long gameId, Double rating, User user);
    Double getUserRatingForGame(Long gameId, Integer userId);
    List<Game> findGamesByTitle(String title);
    void likeGame(Long gameId, Long userId);
}
