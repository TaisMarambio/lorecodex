package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
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
    public Game incrementLikes(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));
        game.setLikes(game.getLikes() + 1);
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
        newGame.setRating(0.0); // o null, según tu lógica
        newGame.setLikes(0);
        newGame.setGenres(request.getGenres());
        newGame.setDevelopersAndPublishers(new HashSet<>());

        return gameRepository.save(newGame);
    }
}
