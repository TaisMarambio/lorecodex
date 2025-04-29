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
        // Validate the rating
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        // Find the game or throw exception
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + gameId));

        try {
            // Save or update the user's rating
            Optional<UserRating> existingRating = userRatingService.getRatingByUserAndGame(user.getId(), gameId);

            UserRating userRating;
            if (existingRating.isPresent()) {
                userRating = existingRating.get();
                userRating.setRating(rating);
            } else {
                userRating = new UserRating();
                userRating.setUser(user);
                userRating.setGame(game);
                userRating.setRating(rating);
            }

            userRatingService.saveOrUpdateRating(userRating);

            // Calculate the new average rating for this game
            Double averageRating = userRatingService.calculateAverageRating(gameId);

            // Update the game's rating with the new average
            game.setRating(averageRating);

            return gameRepository.save(game);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving rating: " + e.getMessage());
        }
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
}