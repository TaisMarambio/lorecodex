package com.lorecodex.backend.service;

import com.lorecodex.backend.model.UserRating;
import java.util.List;
import java.util.Optional;

public interface UserRatingService {
    List<UserRating> getAllRatingsByGameId(Long gameId);
    Optional<UserRating> getRatingByUserAndGame(Long userId, Long gameId);
    UserRating saveOrUpdateRating(UserRating userRating);
    void deleteRating(Long id);
    void deleteRatingByUserAndGame(Long userId, Long gameId);
    Double calculateAverageRating(Long gameId);
}