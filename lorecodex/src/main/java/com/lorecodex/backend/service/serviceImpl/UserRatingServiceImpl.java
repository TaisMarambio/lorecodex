package com.lorecodex.backend.service.serviceImpl;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.UserRatingRepository;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private final UserRatingRepository userRatingRepository;
    private final GameRepository gameRepository;

    @Autowired
    public UserRatingServiceImpl(UserRatingRepository userRatingRepository, GameRepository gameRepository) {
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
        UserRating saved = saveRating(r);

        // Recalcula y guarda el promedio
        Double avg = userRatingRepository.findAverageRatingByGameId(game.getId());
        game.setAverageRating(avg != null ? avg : 0.0);
        // Asegúrate de tener acceso al GameRepository aquí para guardar el cambio
        gameRepository.save(game);
        return saved;
    }

    @Override
    public void deleteRating(User user, Game game) {
        userRatingRepository.deleteByUserAndGame(user, game);

        // Recalcula y guarda el promedio
        Double avg = userRatingRepository.findAverageRatingByGameId(game.getId());
        game.setAverageRating(avg != null ? avg : 0.0);
        gameRepository.save(game);
    }

    @Override
    public Double getAverageRatingByGameId(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;
        return game.getAverageRating();
    }
}