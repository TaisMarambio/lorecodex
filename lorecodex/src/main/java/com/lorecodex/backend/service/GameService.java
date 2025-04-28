package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameResponse;

import java.util.List;

public interface GameService {
    GameResponse createGame(GameRequest request);
    GameResponse getGame(Long id);
    List<GameResponse> getAllGames();
    GameResponse updateGame(Long id, GameRequest request);
    void deleteGame(Long id);
    void likeGame(Long gameId, Long userId);
}
