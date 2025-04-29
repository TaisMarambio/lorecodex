package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    List<Review> getReviewsByGameId(Long gameId);
    List<Review> getReviewsByUserId(Long userId);
    Optional<Review> getReviewById(Long id);
    Optional<Review> getReviewByUserAndGame(Long userId, Long gameId);
    Review createReview(Review review);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
    Review incrementLikes(Long id);
    Review incrementDislikes(Long id);
    boolean canUserModifyReview(User user, Review review);
}