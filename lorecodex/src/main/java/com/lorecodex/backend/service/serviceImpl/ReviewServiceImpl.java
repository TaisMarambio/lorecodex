package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.ReviewRepository;
import com.lorecodex.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByGameId(Long gameId) {
        return reviewRepository.findByGameId(gameId);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Optional<Review> getReviewByUserAndGame(Long userId, Long gameId) {
        return reviewRepository.findByUserIdAndGameId(userId, gameId);
    }

    @Override
    public Review createReview(Review review) {
        // Verificar si el usuario ya ha hecho una review para este juego
        Optional<Review> existingReview = reviewRepository.findByUserIdAndGameId(
                review.getUser().getId(), review.getGame().getId());

        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("User already has a review for this game");
        }

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Review review) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Review not found with ID: " + id);
        }
        review.setId(id);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public Review incrementLikes(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        review.setLikes(review.getLikes() + 1);
        return reviewRepository.save(review);
    }

    @Override
    public Review incrementDislikes(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        review.setDislikes(review.getDislikes() + 1);
        return reviewRepository.save(review);
    }

    @Override
    public boolean canUserModifyReview(User user, Review review) {
        // El usuario solo puede modificar su propia review, a menos que sea admin
        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return isOwner || isAdmin;
    }
}