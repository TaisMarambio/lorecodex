package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.GameService;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final UserRatingService userRatingService;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserRatingService userRatingService) {
        this.gameRepository = gameRepository;
        this.userRatingService = userRatingService;
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Game createGame(Game game) {
        // Initialize default values if not present
        if (game.getRating() == null) {
            game.setRating(0.0);
        }
        if (game.getLikes() == null) {
            game.setLikes(0);
        }
        return gameRepository.save(game);
    }

    @Override
    public Game updateGame(Long id, Game gameDetails) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));

        // Update only if values are not null
        if (gameDetails.getTitle() != null) {
            game.setTitle(gameDetails.getTitle());
        }
        if (gameDetails.getDescription() != null) {
            game.setDescription(gameDetails.getDescription());
        }
        if (gameDetails.getCoverImage() != null) {
            game.setCoverImage(gameDetails.getCoverImage());
        }
        if (gameDetails.getReleaseDate() != null) {
            game.setReleaseDate(gameDetails.getReleaseDate());
        }
        if (gameDetails.getGenres() != null) {
            game.setGenres(gameDetails.getGenres());
        }
        if (gameDetails.getAwards() != null) {
            game.setAwards(gameDetails.getAwards());
        }

        return gameRepository.save(game);
    }

    @Override
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    @Override
    public Game incrementLikes(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));

        int currentLikes = game.getLikes() != null ? game.getLikes() : 0;
        game.setLikes(currentLikes + 1);

        return gameRepository.save(game);
    }

    @Override
    @Transactional
    public Game rateGame(Long gameId, Double rating, User user) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        // Buscar si ya existe rating previo de ese usuario para ese juego
        UserRating userRating = userRatingService.findByUserAndGame(user, game);

        if (userRating == null) {
            // Si no existe, crear nuevo
            userRating = new UserRating();
            userRating.setUser(user);
            userRating.setGame(game);
        }

        // Actualizar o setear el rating
        userRating.setRating(rating);
        userRatingService.save(userRating);

        // Ahora recalculamos el promedio general
        List<UserRating> allRatings = userRatingService.findAllByGame(game);
        double averageRating = allRatings.stream()
                .mapToDouble(UserRating::getRating)
                .average()
                .orElse(0.0);

        game.setRating(averageRating);
        gameRepository.save(game);

        return game;
    }

    @Override
    public Double getUserRatingForGame(Long gameId, Long userId) {
        try {
            Optional<UserRating> userRating = userRatingService.getRatingByUserAndGame(userId, gameId);
            return userRating.map(UserRating::getRating).orElse(null);
        } catch (Exception e) {
            // Log the error if needed
            return null;
        }
    }

    @Override
    public List<Game> findGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Double calculateAverageRating(Long gameId) {
        return userRatingService.calculateAverageRating(gameId);
    }
}