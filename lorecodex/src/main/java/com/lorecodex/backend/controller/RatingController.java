package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.RatingRequest;
import com.lorecodex.backend.dto.response.RatingSummaryDto;
import com.lorecodex.backend.dto.response.UserRatingResponse;
import com.lorecodex.backend.mapper.UserRatingMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rating")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RatingController {

    private final UserRatingService userRatingService;
    private final GameRepository gameRepository;
    private final UserRatingMapper ratingMapper;

    @Autowired
    public RatingController(UserRatingService userRatingService,
                            GameRepository gameRepository,
                            UserRatingMapper ratingMapper) {
        this.userRatingService = userRatingService;
        this.gameRepository = gameRepository;
        this.ratingMapper = ratingMapper;
    }

    /**
     * Endpoint para guardar/actualizar rating (REQUIERE AUTH)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{gameId}")
    public ResponseEntity<UserRatingResponse> setRating(
            @PathVariable Long gameId,
            @RequestBody RatingRequest request,
            @AuthenticationPrincipal User user) {

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserRating rating = userRatingService.rateOrUpdateRating(
                user,
                gameOpt.get(),
                request.getRating()
        );

        return ResponseEntity.ok(ratingMapper.toDTO(rating));
    }

    /**
     * Obtener rating del usuario actual para un juego (REQUIERE AUTH)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/{gameId}")
    public ResponseEntity<UserRatingResponse> getMyRatingForGame(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<UserRating> ratingOpt = userRatingService.getUserRating(user, gameOpt.get());
        return ratingOpt
                .map(r -> ResponseEntity.ok(ratingMapper.toDTO(r)))
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Obtener promedio de ratings (PÚBLICO)
     */
    @GetMapping("/{gameId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long gameId) {
        Double average = userRatingService.getAverageRatingByGameId(gameId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    /**
     * NUEVO: Obtener resumen de rating (promedio + rating del usuario actual)
     * Este endpoint debe funcionar TANTO para usuarios autenticados como NO autenticados
     */
    @GetMapping("/{gameId}/rating-summary")
    public ResponseEntity<RatingSummaryDto> getRatingSummary(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {  // User puede ser null si no está autenticado

        // Obtener promedio (siempre disponible)
        Double average = userRatingService.getAverageRatingByGameId(gameId);

        RatingSummaryDto summary = new RatingSummaryDto();
        summary.setAverage(average != null ? average : 0.0);

        // Si el usuario está autenticado, obtener su rating
        if (user != null) {
            Optional<Game> gameOpt = gameRepository.findById(gameId);
            if (gameOpt.isPresent()) {
                Optional<UserRating> userRating = userRatingService.getUserRating(user, gameOpt.get());
                summary.setMine(userRating.map(UserRating::getRating).orElse(0.0));
            } else {
                summary.setMine(0.0);
            }
        } else {
            // Usuario no autenticado, rating = 0
            summary.setMine(0.0);
        }

        return ResponseEntity.ok(summary);
    }

    /**
     * Obtener todos los ratings de un juego (PÚBLICO)
     */
    @GetMapping("/all/game/{gameId}")
    public ResponseEntity<List<UserRatingResponse>> getAllRatingsForGame(@PathVariable Long gameId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UserRating> ratings = userRatingService.getRatingsByGame(gameOpt.get());
        List<UserRatingResponse> responseList = ratings.stream()
                .map(ratingMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * Obtener todos los ratings del usuario actual (REQUIERE AUTH)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<List<UserRatingResponse>> getAllMyRatings(
            @AuthenticationPrincipal User user) {

        List<UserRating> ratings = userRatingService.getRatingsByUser(user);
        List<UserRatingResponse> responseList = ratings.stream()
                .map(ratingMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * Eliminar rating del usuario actual (REQUIERE AUTH)
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRatingService.deleteRating(user, gameOpt.get());
        return ResponseEntity.noContent().build();
    }
}
