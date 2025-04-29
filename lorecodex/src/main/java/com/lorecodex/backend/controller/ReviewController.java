package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ReviewRequest;
import com.lorecodex.backend.dto.response.ReviewDTO;
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
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    // Obtener todas las reviews (público)
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    // Obtener reviews por juego (público)
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByGame(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    // Obtener reviews por usuario (requiere autenticación)
    @GetMapping("/user")
    public ResponseEntity<List<ReviewDTO>> getCurrentUserReviews(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Review> reviews = reviewService.getReviewsByUserId(user.getId());
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }

    // Obtener una review específica (publico)
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(reviewMapper.toDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    //crear una review (requiere autenticación)
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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

    // Actualizar una review (requiere ser el propietario o admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestBody ReviewRequest reviewRequest,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return reviewService.getReviewById(id)
                .map(existingReview -> {
                    // Verificar si el usuario puede modificar esta review
                    if (!reviewService.canUserModifyReview(user, existingReview)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You don't have permission to modify this review");
                    }

                    // Si el usuario no es el propietario pero es admin, solo puede eliminar, no editar
                    if (!existingReview.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Admins can only delete reviews, not edit them");
                    }

                    reviewMapper.updateEntityFromRequest(existingReview, reviewRequest);
                    Review updatedReview = reviewService.updateReview(id, existingReview);
                    return ResponseEntity.ok(reviewMapper.toDTO(updatedReview));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar una review (requiere ser el propietario o admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return reviewService.getReviewById(id)
                .map(review -> {
                    // Verificar si el usuario puede eliminar esta review
                    if (!reviewService.canUserModifyReview(user, review)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You don't have permission to delete this review");
                    }

                    reviewService.deleteReview(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para dar like a una review (público)
    @PostMapping("/{id}/like")
    public ResponseEntity<ReviewDTO> likeReview(@PathVariable Long id) {
        Review likedReview = reviewService.incrementLikes(id);
        return ResponseEntity.ok(reviewMapper.toDTO(likedReview));
    }

    // Endpoint para dar dislike a una review (público)
    @PostMapping("/{id}/dislike")
    public ResponseEntity<ReviewDTO> dislikeReview(@PathVariable Long id) {
        Review dislikedReview = reviewService.incrementDislikes(id);
        return ResponseEntity.ok(reviewMapper.toDTO(dislikedReview));
    }

    // Endpoint admin para ver todas las reviews con control adicional
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsAdmin() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewMapper.toDTOList(reviews));
    }
}