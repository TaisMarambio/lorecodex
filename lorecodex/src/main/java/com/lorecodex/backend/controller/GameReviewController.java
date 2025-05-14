package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ReviewRequest;
import com.lorecodex.backend.dto.response.ReviewResponse;
import com.lorecodex.backend.mapper.ReviewMapper;
import com.lorecodex.backend.model.Review;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public GameReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    // ------------------------ Públicos ------------------------

    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByGame(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(reviewMapper.toDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public ResponseEntity<ReviewResponse> likeReview(@PathVariable Long id) {
        Review likedReview = reviewService.incrementLikes(id);
        return ResponseEntity.ok(reviewMapper.toDTO(likedReview));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/dislike")
    public ResponseEntity<ReviewResponse> dislikeReview(@PathVariable Long id) {
        Review dislikedReview = reviewService.incrementDislikes(id);
        return ResponseEntity.ok(reviewMapper.toDTO(dislikedReview));
    }

    // ------------------------ Requieren autenticación ------------------------

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getCurrentUserReviews(@AuthenticationPrincipal User user) {
        List<Review> reviews = reviewService.getReviewsByUserId(user.getId());
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/game/{gameId}/createReview")
    public ResponseEntity<?> createReviewForGame(@PathVariable Long gameId,
                                                 @RequestBody ReviewRequest reviewRequest,
                                                 @AuthenticationPrincipal User user) {
        reviewRequest.setGameId(gameId);
        try {
            Review review = reviewMapper.toEntity(reviewRequest, user);
            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toDTO(savedReview));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestBody ReviewRequest reviewRequest,
                                          @AuthenticationPrincipal User user) {
        return reviewService.getReviewById(id)
                .map(existingReview -> {
                    if (!reviewService.canUserModifyReview(user, existingReview)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to modify this review");
                    }

                    if (!existingReview.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admins can only delete reviews, not edit them");
                    }

                    reviewMapper.updateEntityFromRequest(existingReview, reviewRequest);
                    Review updatedReview = reviewService.updateReview(id, existingReview);
                    return ResponseEntity.ok(reviewMapper.toDTO(updatedReview));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id,
                                          @AuthenticationPrincipal User user) {

        return reviewService.getReviewById(id)
                .map(review -> {
                    if (!reviewService.canUserModifyReview(user, review)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete this review");
                    }

                    reviewService.deleteReview(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------ Admin ------------------------

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsAdmin() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }
}
