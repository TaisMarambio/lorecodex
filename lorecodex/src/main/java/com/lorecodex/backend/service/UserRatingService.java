package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import java.util.List;
import java.util.Optional;

public interface UserRatingService {
    Optional<UserRating> getUserRating(User user, Game game);

    List<UserRating> getRatingsByUser(User user);

    List<UserRating> getRatingsByGame(Game game);

    UserRating saveRating(UserRating userRating);

    UserRating rateOrUpdateRating(User user, Game game, Double rating);

    void deleteRating(User user, Game game);
}