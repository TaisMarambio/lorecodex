package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.ReviewRepository;
import com.lorecodex.backend.repository.UserRatingRepository;
import com.lorecodex.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRatingRepository userRatingRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRatingRepository userRatingRepository) {
        this.reviewRepository = reviewRepository;
        this.userRatingRepository = userRatingRepository;
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
    @Transactional
    public Review createReview(Review review) {
        // VALIDACIÓN: El usuario debe tener un rating para este juego
        Optional<UserRating> userRating = userRatingRepository.findByUserAndGame(
                review.getUser(),
                review.getGame()
        );

        if (userRating.isEmpty()) {
            throw new IllegalArgumentException(
                    "You must rate this game before writing a review. Please add a rating first."
            );
        }

        // Verificar si el usuario ya tiene una review para este juego
        Optional<Review> existingReview = reviewRepository.findByUserIdAndGameId(
                review.getUser().getId(),
                review.getGame().getId()
        );

        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("User already has a review for this game");
        }

        // Sincronizar el rating de la review con el UserRating
        review.setRating(userRating.get().getRating());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review review) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Review not found with ID: " + id);
        }

        // Al actualizar, también sincronizamos con el UserRating actual
        Optional<UserRating> userRating = userRatingRepository.findByUserAndGame(
                review.getUser(),
                review.getGame()
        );

        if (userRating.isPresent()) {
            review.setRating(userRating.get().getRating());
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
        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return isOwner || isAdmin;
    }
}
