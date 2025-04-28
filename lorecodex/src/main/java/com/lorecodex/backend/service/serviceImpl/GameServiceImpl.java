package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Inicializar valores por defecto si no están presentes
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

        // Actualizar solo si los valores no son nulos
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
    public Game updateRating(Long id, Double newRating) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + id));

        // Aquí podrías implementar lógica para calcular el promedio de ratings
        // Por ahora, simplemente actualizamos el rating
        game.setRating(newRating);

        return gameRepository.save(game);
    }

    @Override
    public List<Game> findGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }
}