package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.service.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    public GameResponse mapToGameResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .rating(game.getRating())
                .genre(game.getGenre())
                .description(game.getDescription())
                .build();
    }

    @Override
    public GameResponse createGame(GameRequest request) {
        return null;
    }

    @Override
    public GameResponse getGame(Long id) {
        return null;
    }

    @Override
    public List<GameResponse> getAllGames() {
        return List.of();
    }

    @Override
    public GameResponse updateGame(Long id, GameRequest request) {
        return null;
    }

    @Override
    public void deleteGame(Long id) {

    }

    @Override
    public void likeGame(Long gameId, Long userId) {

    }
}
