package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.mapper.GameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    // Endpoint con paginación
    @GetMapping("/allGames")
    public ResponseEntity<List<GameDetailResponse>> getAllGames(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage;

        if (title != null && !title.isEmpty()) {
            gamesPage = gameService.findGamesByTitle(title, pageable);
        } else {
            gamesPage = gameService.getAllGamesPaginated(pageable);
        }

        return ResponseEntity.ok(gameMapper.toDTOList(gamesPage.getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailResponse> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity.ok(gameMapper.toDTO(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GameDetailResponse> createGame(@RequestBody GameRequest gameRequest) {
        Game game = gameMapper.toEntity(gameRequest);
        Game savedGame = gameService.createGame(game);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameMapper.toDTO(savedGame));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GameDetailResponse> updateGame(
            @PathVariable Long id,
            @RequestBody GameRequest gameRequest) {
        return gameService.getGameById(id)
                .map(existingGame -> {
                    gameMapper.updateEntityFromRequest(existingGame, gameRequest);
                    Game updatedGame = gameService.updateGame(id, existingGame);
                    return ResponseEntity.ok(gameMapper.toDTO(updatedGame));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (gameService.getGameById(id).isPresent()) {
            gameService.deleteGame(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<GameDetailResponse> likeGame(@PathVariable Long id) {
        Game likedGame = gameService.incrementLikes(id);
        return ResponseEntity.ok(gameMapper.toDTO(likedGame));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameDetailResponse>> searchGamesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Game> games = gameService.findGamesByTitle(title, pageable);
        return ResponseEntity.ok(gameMapper.toDTOList(games.getContent()));
    }
}
