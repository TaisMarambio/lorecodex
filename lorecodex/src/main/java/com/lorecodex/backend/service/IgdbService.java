package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.model.Game;

import java.util.List;
import java.util.Optional;

public interface IgdbService {
    String getTopGames();
    List<IgdbGameResponse> searchGames(String query);
    Game importGameFromIgdb(CreateGameFromIgdbRequest request);
    Optional<IgdbGameResponse> getGameById(Long igdbId);
    Optional<Game> importGameById(Long igdbId);
    List<IgdbGameResponse> getTopGamesList();
}
