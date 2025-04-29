package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
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
    Double calculateUserRating(Long userId, Long gameId);
    UserRating findByUserAndGame(User user, Game game);
    void save(UserRating userRating);
    List<UserRating> findAllByGame(Game game);
}