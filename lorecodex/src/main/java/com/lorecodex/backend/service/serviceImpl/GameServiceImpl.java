package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.GameNoteRepository;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameNoteRepository gameNoteRepository;
    private static final int POPULAR_LIKES_THRESHOLD = 1_000;
    private static final String POPULAR_TAG = "Popular";

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, GameNoteRepository gameNoteRepository) {
        this.gameRepository = gameRepository;
        this.gameNoteRepository = gameNoteRepository;
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Page<Game> getAllGamesPaginated(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Game createGame(Game game) {
        if (game.getRating() == null) {
            game.setRating(0.0);
        }
        if (game.getLikes() == null) {
            game.setLikes(0);
        }
        ensureTagCollection(game);
        updatePopularTag(game);
        return gameRepository.save(game);
    }

    @Override
    public Game updateGame(Long id, Game gameDetails) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));

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
        if (gameDetails.getDevelopersAndPublishers() != null) {
            game.setDevelopersAndPublishers(gameDetails.getDevelopersAndPublishers());
        }

        return gameRepository.save(game);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        // Verificar que el juego existe
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));

        // Eliminar el juego (el cascade se encargará de las notas, reviews, ratings, etc.)
        gameRepository.delete(game);
    }

    @Override
    public List<Game> findGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Page<Game> findGamesByTitle(String title, Pageable pageable) {
        return gameRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Page<Game> findGamesByTag(String tag, Pageable pageable) {
        return gameRepository.findByTagsContainingIgnoreCase(tag, pageable);
    }

    @Override
    public Game incrementLikes(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));
        game.setLikes(game.getLikes() + 1);
        updatePopularTag(game);
        return gameRepository.save(game);
    }

    @Override
    public Game importGameFromIgdb(CreateGameFromIgdbRequest request) {
        Optional<Game> existingGame = gameRepository.findByTitleIgnoreCase(request.getTitle());

        if (existingGame.isPresent()) {
            return existingGame.get();
        }

        Game newGame = new Game();
        newGame.setTitle(request.getTitle());
        newGame.setDescription(request.getDescription());
        newGame.setCoverImage(request.getCoverImage());
        newGame.setReleaseDate(request.getReleaseDate());
        newGame.setRating(0.0);
        newGame.setLikes(0);
        newGame.setGenres(request.getGenres());
        newGame.setDevelopersAndPublishers(new HashSet<>());
        ensureTagCollection(newGame);
        updatePopularTag(newGame);

        return gameRepository.save(newGame);
    }

    @Override
    public Set<String> getAllUniqueGenres() {
        List<Game> allGames = gameRepository.findAll();
        Set<String> uniqueGenres = new TreeSet<>();

        for (Game game : allGames) {
            if (game.getGenres() != null && !game.getGenres().isEmpty()) {
                uniqueGenres.addAll(game.getGenres());
            }
        }

        return uniqueGenres;
    }

    private void ensureTagCollection(Game game) {
        if (game.getTags() == null) {
            game.setTags(new HashSet<>());
        }
    }

    private void updatePopularTag(Game game) {
        ensureTagCollection(game);
        if (game.getLikes() >= POPULAR_LIKES_THRESHOLD) {
            game.getTags().add(POPULAR_TAG);
        } else {
            game.getTags().remove(POPULAR_TAG);
        }
    }
}
