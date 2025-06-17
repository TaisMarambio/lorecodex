package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Game createGame(Game game) {
        // Initialize default values if not present
        if (game.getAverageRating() == null) {
            game.setAverageRating(0.0);
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
    public Game incrementLikes(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));
        game.setLikes(game.getLikes() + 1);
        return gameRepository.save(game);
    }

    @Override
    public Game importGameFromIgdb(CreateGameFromIgdbRequest request) {
        // Verificamos si ya existe en la base por título (podés usar ID de IGDB si lo guardás)
        Optional<Game> existingGame = gameRepository.findByTitleIgnoreCase(request.getTitle());

        if (existingGame.isPresent()) {
            return existingGame.get(); // ya lo tenés guardado
        }

        Game newGame = new Game();
        newGame.setTitle(request.getTitle());
        newGame.setDescription(request.getDescription());
        newGame.setCoverImage(request.getCoverImage());
        newGame.setReleaseDate(request.getReleaseDate());
        newGame.setAverageRating(0.0); // o null, según tu lógica
        newGame.setLikes(0);
        newGame.setGenres(request.getGenres());
        newGame.setDevelopersAndPublishers(new HashSet<>());

        return gameRepository.save(newGame);
    }
}