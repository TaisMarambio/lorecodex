package com.lorecodex.backend.controller;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/utility")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UtilityController {

    private final GameRepository gameRepository;
    private final UserRatingRepository userRatingRepository;

    @Autowired
    public UtilityController(GameRepository gameRepository, UserRatingRepository userRatingRepository) {
        this.gameRepository = gameRepository;
        this.userRatingRepository = userRatingRepository;
    }

    @PostMapping("/recalculate-ratings")
    public ResponseEntity<Map<String, Object>> recalculateAllRatings() {
        List<Game> allGames = gameRepository.findAll();
        int updated = 0;
        int withRatings = 0;
        int withoutRatings = 0;

        for (Game game : allGames) {
            List<UserRating> ratings = userRatingRepository.findByGame(game);

            if (ratings != null && !ratings.isEmpty()) {
                double average = ratings.stream()
                        .mapToDouble(UserRating::getRating)
                        .average()
                        .orElse(0.0);

                game.setRating(average);
                withRatings++;
            } else {
                game.setRating(0.0);
                withoutRatings++;
            }

            gameRepository.save(game);
            updated++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalGames", allGames.size());
        result.put("updated", updated);
        result.put("gamesWithRatings", withRatings);
        result.put("gamesWithoutRatings", withoutRatings);
        result.put("message", "Ratings recalculated successfully");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/game-stats")
    public ResponseEntity<List<Map<String, Object>>> getGameStats() {
        List<Game> games = gameRepository.findAll();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Game game : games) {
            Map<String, Object> gameStat = new HashMap<>();
            gameStat.put("id", game.getId());
            gameStat.put("title", game.getTitle());
            gameStat.put("rating", game.getRating());
            gameStat.put("ratingCount", game.getUserRatings() != null ? game.getUserRatings().size() : 0);
            gameStat.put("genres", game.getGenres());
            stats.add(gameStat);
        }

        return ResponseEntity.ok(stats);
    }
}