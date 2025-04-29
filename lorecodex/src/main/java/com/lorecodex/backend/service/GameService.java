package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import java.util.List;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();
    Optional<Game> getGameById(Long id);
    Game createGame(Game game);
    Game updateGame(Long id, Game game);
    void deleteGame(Long id);
    Game incrementLikes(Long id);
    Game rateGame(Long gameId, Double rating, User user);
    Double getUserRatingForGame(Long gameId, Integer userId);
    List<Game> findGamesByTitle(String title);
}