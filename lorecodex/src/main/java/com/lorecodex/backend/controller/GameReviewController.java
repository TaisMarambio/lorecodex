package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ReviewRequest;
import com.lorecodex.backend.dto.response.ReviewDTO;
import com.lorecodex.backend.mapper.ReviewMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final GameRepository gameRepository;

    @Autowired
    public GameReviewController(ReviewService reviewService, ReviewMapper reviewMapper, GameRepository gameRepository) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
        this.gameRepository = gameRepository;
    }

    // Get all reviews for a specific game
    @GetMapping("/{gameId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByGame(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    // Create a review for a specific game
    @PostMapping("/{gameId}/reviews")
    public ResponseEntity<?> createReviewForGame(
            @PathVariable Long gameId,
            @RequestBody ReviewRequest reviewRequest,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Verify the game exists
        Game game = gameRepository.findById(gameId)
                .orElse(null);

        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        // Update the request with the game ID
        reviewRequest.setGameId(gameId);

        try {
            Review review = reviewMapper.toEntity(reviewRequest, user);
            Review savedReview = reviewService.createReview(review);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(reviewMapper.toDTO(savedReview));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}