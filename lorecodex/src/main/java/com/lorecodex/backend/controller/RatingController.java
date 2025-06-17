package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.RatingSummaryDto;
import com.lorecodex.backend.dto.request.RatingRequest;
import com.lorecodex.backend.dto.response.UserRatingResponse;
import com.lorecodex.backend.mapper.UserRatingMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.UserRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

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
    public RatingController(UserRatingService userRatingService, GameRepository gameRepository, UserRatingMapper ratingMapper) {
        this.userRatingService = userRatingService;
        this.gameRepository = gameRepository;
        this.ratingMapper = ratingMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{gameId}")
    public ResponseEntity<UserRatingResponse> rateGame(
            @PathVariable Long gameId,
            @RequestBody @Valid RatingRequest dto,
            @AuthenticationPrincipal User user) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserRating saved = userRatingService.rateOrUpdateRating(user, game, dto.getRating());
        return ResponseEntity.ok(ratingMapper.toDTO(saved));
    }

    @GetMapping("/{gameId}/rating-summary")
    public ResponseEntity<RatingSummaryDto> getRatingSummary(@PathVariable Long gameId, @AuthenticationPrincipal User user) {
        // 1) Aseguramos que el juego exista
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 2) Obtenemos el promedio, usando 0.0 si viene null
        Double avgObj = userRatingService.getAverageRatingByGameId(gameId);
            double avg = (avgObj != null) ? avgObj : 0.0;

        // 3) Obtenemos mi rating (puede ser null)
        Integer mine = userRatingService
                .getUserRating(user, game)
                .map(UserRating::getRating)
                .map(Double::intValue)
                .orElse(null);

        return ResponseEntity.ok(new RatingSummaryDto(avg, mine));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/{gameId}")
    public ResponseEntity<?> getMyRatingForGame(@PathVariable Long gameId,
                                                @AuthenticationPrincipal User user) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();

        Optional<UserRating> ratingOpt = userRatingService.getUserRating(user, gameOpt.get());
        return ratingOpt.map(r -> ResponseEntity.ok(ratingMapper.toDTO(r)))
                .orElse(ResponseEntity.noContent().build());
    }

    /*@GetMapping("/all/game/{gameId}")
    public ResponseEntity<?> getAllRatingsForGame(@PathVariable Long gameId) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();

        List<UserRating> ratings = userRatingService.getRatingsByGame(gameOpt.get());
        List<UserRatingResponse> responseList = ratings.stream()
                .map(ratingMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }*/

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<?> getAllRatingsByCurrentUser(@AuthenticationPrincipal User user) {

        List<UserRating> ratings = userRatingService.getRatingsByUser(user);
        List<UserRatingResponse> responseList = ratings.stream()
                .map(ratingMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long gameId,
                                          @AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) return ResponseEntity.notFound().build();

        userRatingService.deleteRating(user, gameOpt.get());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{gameId}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable Long gameId) {
        Double average = userRatingService.getAverageRatingByGameId(gameId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

}
