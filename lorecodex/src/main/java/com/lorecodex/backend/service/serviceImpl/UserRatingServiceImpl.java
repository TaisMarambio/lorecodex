package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.UserRatingRepository;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private final UserRatingRepository userRatingRepository;
    private final GameRepository gameRepository;

    @Autowired
    public UserRatingServiceImpl(UserRatingRepository userRatingRepository,
                                 GameRepository gameRepository) {
        this.userRatingRepository = userRatingRepository;
        this.gameRepository = gameRepository;
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
    @Transactional
    public UserRating saveRating(UserRating userRating) {
        UserRating saved = userRatingRepository.save(userRating);
        updateGameRatingStats(userRating.getGame());
        return saved;
    }

    @Override
    @Transactional
    public UserRating rateOrUpdateRating(User user, Game game, Double rating) {
        Optional<UserRating> existing = getUserRating(user, game);
        UserRating r = existing.orElseGet(UserRating::new);
        r.setUser(user);
        r.setGame(game);
        r.setRating(rating);
        UserRating saved = saveRating(r);
        updateGameRatingStats(game);
        return saved;
    }

    @Override
    @Transactional
    public void deleteRating(User user, Game game) {
        userRatingRepository.deleteByUserAndGame(user, game);
        updateGameRatingStats(game);
    }

    @Override
    public Double getAverageRatingByGameId(Long gameId) {
        return userRatingRepository.findAverageRatingByGameId(gameId);
    }

    // ADDED: Update game rating statistics
    @Transactional
    protected void updateGameRatingStats(Game game) {
        List<UserRating> ratings = userRatingRepository.findByGame(game);
        int count = ratings.size();
        Double average = count > 0 ?
                ratings.stream()
                        .mapToDouble(UserRating::getRating)
                        .average()
                        .orElse(0.0) : 0.0;

        game.setRating(average);
        game.updateRatingCount(count);
        gameRepository.save(game);
    }
}
