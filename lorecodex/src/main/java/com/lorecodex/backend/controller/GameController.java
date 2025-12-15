package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.dto.response.PagedResponse;
import com.lorecodex.backend.mapper.GameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
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
    public ResponseEntity<PagedResponse<GameDetailResponse>> getAllGames(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "0") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false, defaultValue = "rating,desc") String sort) {

        int safePage = parsePage(page, 0);
        int safeSize = Math.max(1, parsePage(size, 12));
        Sort sortSpec = parseSort(sort);
        Pageable pageable = PageRequest.of(safePage, safeSize, sortSpec);
        Page<Game> gamesPage;

        if (StringUtils.hasText(tag)) {
            gamesPage = gameService.findGamesByTag(tag, pageable);
        } else if (StringUtils.hasText(title)) {
            gamesPage = gameService.findGamesByTitle(title, pageable);
        } else {
            gamesPage = gameService.getAllGamesPaginated(pageable);
        }

        return ResponseEntity.ok(PagedResponse.from(gamesPage, gameMapper.toDTOList(gamesPage.getContent())));
    }

    private int parsePage(String raw, int fallback) {
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Sort parseSort(String rawSort) {
        if (!StringUtils.hasText(rawSort)) {
            return Sort.by(Sort.Direction.DESC, "rating");
        }
        String[] parts = rawSort.split(",");
        Sort.Direction direction = Sort.Direction.DESC;
        if (parts.length > 1 && parts[1].equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }
        String property = parts[0].trim();
        if (property.isEmpty()) {
            property = "rating";
        }
        return Sort.by(direction, property);
        }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailResponse> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity.ok(gameMapper.toDTO(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDetailResponse> createGame(@RequestBody GameRequest gameRequest) {
        Game game = gameMapper.toEntity(gameRequest);
        Game savedGame = gameService.createGame(game);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameMapper.toDTO(savedGame));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
