package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.ReviewRequest;
import com.lorecodex.backend.dto.response.ReviewResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    private final GameRepository gameRepository;

    @Autowired
    public ReviewMapper(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public ReviewResponse toDTO(Review review) {
        if (review == null) {
            return null;
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .likes(review.getLikes())
                .dislikes(review.getDislikes())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .username(review.getUser() != null ? review.getUser().getUsername() : null)
                .gameId(review.getGame() != null ? review.getGame().getId() : null)
                .gameTitle(review.getGame() != null ? review.getGame().getTitle() : null)
                .build();
    }

    public List<ReviewResponse> toDTOList(List<Review> reviews) {
        if (reviews == null) {
            return List.of();
        }

        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Review toEntity(ReviewRequest reviewRequest, User user) {
        if (reviewRequest == null) {
            return null;
        }

        Review review = new Review();
        review.setContent(reviewRequest.getContent());
        review.setRating(reviewRequest.getRating());
        review.setLikes(0);
        review.setDislikes(0);
        review.setUser(user);

        if (reviewRequest.getGameId() != null) {
            Game game = gameRepository.findById(reviewRequest.getGameId())
                    .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + reviewRequest.getGameId()));
            review.setGame(game);
        }

        return review;
    }

    public void updateEntityFromRequest(Review review, ReviewRequest reviewRequest) {
        if (reviewRequest.getContent() != null) {
            review.setContent(reviewRequest.getContent());
        }
        if (reviewRequest.getRating() != null) {
            review.setRating(reviewRequest.getRating());
        }
        if (reviewRequest.getGameId() != null &&
                (review.getGame() == null || !review.getGame().getId().equals(reviewRequest.getGameId()))) {
            Game game = gameRepository.findById(reviewRequest.getGameId())
                    .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + reviewRequest.getGameId()));
            review.setGame(game);
        }
    }
}