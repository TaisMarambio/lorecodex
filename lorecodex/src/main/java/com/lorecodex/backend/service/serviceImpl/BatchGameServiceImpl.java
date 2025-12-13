package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.BatchGameRequest;
import com.lorecodex.backend.dto.request.GameRequest;
import com.lorecodex.backend.dto.response.BatchGameResponse;
import com.lorecodex.backend.mapper.GameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.BatchGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchGameServiceImpl implements BatchGameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    @Override
    @Transactional
    public BatchGameResponse importGamesInBatch(BatchGameRequest request) {
        List<BatchGameResponse.GameImportResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        log.info("Starting batch import of {} games", request.getGames().size());

        for (GameRequest gameRequest : request.getGames()) {
            try {
                // Validar campos requeridos
                if (gameRequest.getTitle() == null || gameRequest.getTitle().trim().isEmpty()) {
                    results.add(BatchGameResponse.GameImportResult.builder()
                            .title("Unknown")
                            .success(false)
                            .message("Title is required")
                            .build());
                    failureCount++;
                    continue;
                }

                // Verificar si el juego ya existe
                if (gameRepository.findByTitleIgnoreCase(gameRequest.getTitle()).isPresent()) {
                    results.add(BatchGameResponse.GameImportResult.builder()
                            .title(gameRequest.getTitle())
                            .success(false)
                            .message("Game already exists with this title")
                            .build());
                    failureCount++;
                    log.warn("Game already exists: {}", gameRequest.getTitle());
                    continue;
                }

                // Crear el juego
                Game game = new Game();
                game.setTitle(gameRequest.getTitle());
                game.setDescription(gameRequest.getDescription());
                game.setCoverImage(gameRequest.getCoverImage());
                game.setReleaseDate(gameRequest.getReleaseDate());

                // Manejar géneros - convertir String a Set
                if (gameRequest.getGenre() != null && !gameRequest.getGenre().trim().isEmpty()) {
                    game.setGenres(new HashSet<>(List.of(gameRequest.getGenre())));
                } else if (gameRequest.getGenres() != null && !gameRequest.getGenres().isEmpty()) {
                    game.setGenres(gameRequest.getGenres());
                } else {
                    game.setGenres(new HashSet<>());
                }

                // Valores por defecto obligatorios
                game.setRating(0.0);
                game.setLikes(0);
                game.setDevelopersAndPublishers(new HashSet<>());

                Game savedGame = gameRepository.save(game);

                results.add(BatchGameResponse.GameImportResult.builder()
                        .title(savedGame.getTitle())
                        .success(true)
                        .message("Game imported successfully")
                        .gameId(savedGame.getId())
                        .build());
                successCount++;

                log.info("Successfully imported game: {} (ID: {})", savedGame.getTitle(), savedGame.getId());

            } catch (Exception e) {
                results.add(BatchGameResponse.GameImportResult.builder()
                        .title(gameRequest.getTitle() != null ? gameRequest.getTitle() : "Unknown")
                        .success(false)
                        .message("Error: " + e.getMessage())
                        .build());
                failureCount++;

                log.error("Failed to import game: {}", gameRequest.getTitle(), e);
            }
        }

        log.info("Batch import completed. Success: {}, Failures: {}", successCount, failureCount);

        return BatchGameResponse.builder()
                .totalProcessed(request.getGames().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }
}
