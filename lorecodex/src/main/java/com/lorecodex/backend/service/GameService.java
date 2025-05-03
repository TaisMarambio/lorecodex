package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();
    Optional<Game> getGameById(Long id);
    Game createGame(Game game);
    Game updateGame(Long id, Game game);
    void deleteGame(Long id);
    List<Game> findGamesByTitle(String title);
    Game incrementLikes(Long id);
    Game importGameFromIgdb(CreateGameFromIgdbRequest request);
}