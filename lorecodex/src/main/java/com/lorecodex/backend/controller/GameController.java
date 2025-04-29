package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.request.RatingRequest;
import com.lorecodex.backend.dto.response.GameDTO;
import com.lorecodex.backend.dto.response.UserRatingDTO;
import com.lorecodex.backend.mapper.GameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @Autowired
    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    // Endpoint público para obtener todos los juegos
    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames(@RequestParam(required = false) String title) {
        List<Game> games;

        if (title != null && !title.isEmpty()) {
            // Si se proporciona un título, buscar por título
            games = gameService.findGamesByTitle(title);
        } else {
            // De lo contrario, obtener todos los juegos
            games = gameService.getAllGames();
        }

        return ResponseEntity.ok(gameMapper.toDTOList(games));
    }

    // Endpoint público para obtener un juego por ID
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity.ok(gameMapper.toDTO(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo los administradores pueden crear juegos
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameRequest gameRequest) {
        Game game = gameMapper.toEntity(gameRequest);
        Game savedGame = gameService.createGame(game);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameMapper.toDTO(savedGame));
    }

    // Solo los administradores pueden actualizar juegos
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @RequestBody GameRequest gameRequest) {
        return gameService.getGameById(id)
                .map(existingGame -> {
                    gameMapper.updateEntityFromRequest(existingGame, gameRequest);
                    Game updatedGame = gameService.updateGame(id, existingGame);
                    return ResponseEntity.ok(gameMapper.toDTO(updatedGame));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo los administradores pueden eliminar juegos
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (gameService.getGameById(id).isPresent()) {
            gameService.deleteGame(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint para dar likes a un juego (puede ser público)
    @PostMapping("/{id}/like")
    public ResponseEntity<GameDTO> likeGame(@PathVariable Long id) {
        Game likedGame = gameService.incrementLikes(id);
        return ResponseEntity.ok(gameMapper.toDTO(likedGame));
    }

    // Endpoint para obtener el rating del usuario para un juego específico
    @GetMapping("/{id}/rating")
    public ResponseEntity<?> getUserRating(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Double userRating = gameService.getUserRatingForGame(id, user.getId());
        if (userRating == null) {
            return ResponseEntity.ok(new UserRatingDTO(id, 0.0));
        }

        return ResponseEntity.ok(new UserRatingDTO(id, userRating));
    }

    // Endpoint para actualizar el rating de un juego (requiere autenticación)
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateGame(@PathVariable Long id,
                                      @RequestBody RatingRequest ratingRequest,
                                      @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Double rating = ratingRequest.getRating();
        if (rating == null || rating < 0 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 0 and 5");
        }

        try {
            Game ratedGame = gameService.rateGame(id, rating, user);
            return ResponseEntity.ok(gameMapper.toDTO(ratedGame));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint público para obtener el promedio de rating de un juego
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double averageRating = gameService.getGameById(id)
                .map(game -> gameService.calculateAverageRating(game.getId()))
                .orElse(0.0);

        return ResponseEntity.ok(averageRating);
    }

}