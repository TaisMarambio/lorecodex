package com.lorecodex.backend.service.serviceImpl;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.UserRatingRepository;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private final UserRatingRepository userRatingRepository;

    @Autowired
    public UserRatingServiceImpl(UserRatingRepository userRatingRepository) {
        this.userRatingRepository = userRatingRepository;
    }

    @Override
    public Optional<UserRating> getUserRating(User user, Game game) {
        return userRatingRepository.findByUserAndGame(user, game);
    }

    @Override
    public List<UserRating> getRatingsByUser(User user) {
        return userRatingRepository.findByUser(user);
    }

    @Override
    public List<UserRating> getRatingsByGame(Game game) {
        return userRatingRepository.findByGame(game);
    }

    @Override
    public UserRating saveRating(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }

    @Override
    public UserRating rateOrUpdateRating(User user, Game game, Double rating) {
        Optional<UserRating> existing = getUserRating(user, game);
        UserRating r = existing.orElseGet(UserRating::new);
        r.setUser(user);
        r.setGame(game);
        r.setRating(rating);
        return saveRating(r);
    }

    @Override
    public void deleteRating(User user, Game game) {
        userRatingRepository.deleteByUserAndGame(user, game);
    }

    @Override
    public Double getAverageRatingByGameId(Long gameId) {
        return userRatingRepository.findAverageRatingByGameId(gameId);
    }
}