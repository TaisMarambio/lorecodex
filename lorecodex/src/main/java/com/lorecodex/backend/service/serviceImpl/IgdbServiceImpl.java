package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.dto.response.igdb.GenreResponse;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.dto.response.igdb.KeywordResponse;
import com.lorecodex.backend.mapper.IgdbGameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.IgdbService;
import com.lorecodex.backend.service.TwitchAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IgdbServiceImpl implements IgdbService {
    private final TwitchAuthService authService;
    private final WebClient webClient;
    private final GameRepository gameRepository;
    private final IgdbGameMapper igdbGameMapper;

    @Value("${igdb.client-id}")
    private String clientId;

    private static final String IGDB_FIELDS = """
            fields id, name, summary, cover.url, genres.name, release_dates.date, involved_companies.company.name, rating, keywords.name;
            """;

    public IgdbServiceImpl(TwitchAuthService authService, GameRepository gameRepository, IgdbGameMapper igdbGameMapper) {
        this.authService = authService;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.igdb.com/v4")
                .defaultHeader("Accept", "application/json")
                .build();
        this.gameRepository = gameRepository;
        this.igdbGameMapper = igdbGameMapper;
    }

    @Override
    public List<IgdbGameResponse> getTopGames(int page, int size) {
        String body = """
                %s
                sort rating desc;
                """.formatted(IGDB_FIELDS);

        return fetchGames(body, page, size);
    }

    @Override
    public List<IgdbGameResponse> searchGames(String query, int page, int size) {
        String body = """
                search "%s";
                %s
                """.formatted(query, IGDB_FIELDS);

        return fetchGames(body, page, size);
    }

    @Override
    public Game importGameFromIgdb(CreateGameFromIgdbRequest request) {
        Optional<Game> existing = gameRepository.findByIgdbId(request.getIgdbId());
        if (existing.isPresent()) return existing.get();

        Game game = new Game();
        game.setIgdbId(request.getIgdbId());
        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setCoverImage(request.getCoverImage());
        game.setReleaseDate(request.getReleaseDate());
        game.setGenres(request.getGenres() != null ? request.getGenres() : Set.of());
        game.setDevelopersAndPublishers(Set.of()); // vacío por ahora
        game.setRating(0.0);
        game.setLikes(0);

        return gameRepository.save(game);
    }

    @Override
    public Optional<IgdbGameResponse> getGameById(Long igdbId) {
        String body = """
                %s
                where id = %d;
                limit 1;
                """.formatted(IGDB_FIELDS, igdbId);

        List<IgdbGameResponse> games = fetchGames(body, 0, 1);
        return games.stream().findFirst();
    }

    @Override
    public Optional<Game> importGameById(Long igdbId) {
        Optional<IgdbGameResponse> optional = getGameById(igdbId);
        if (optional.isEmpty()) return Optional.empty();

        IgdbGameResponse igdbGame = optional.get();

        Optional<Game> existing = gameRepository.findByIgdbId(igdbGame.getIgdbId());
        if (existing.isPresent()) return Optional.of(existing.get());

        Game game = new Game();
        game.setIgdbId(igdbGame.getIgdbId());
        game.setTitle(igdbGame.getName());
        game.setDescription(igdbGame.getSummary());
        game.setCoverImage(
                igdbGame.getCover() != null
                        ? "https:" + igdbGame.getCover().getUrl()
                        : null
        );
        game.setGenres(igdbGame.getGenres() != null
                ? igdbGame.getGenres().stream()
                .map(GenreResponse::getName)
                .collect(Collectors.toSet())
                : Set.of()
        );
        game.setTags(extractKeywords(igdbGame));
        game.setReleaseDate(
                igdbGameMapper.getFirstReleaseDate(igdbGame)
        );
        game.setDevelopersAndPublishers(igdbGame.getInvolvedCompanies() != null
                ? igdbGame.getInvolvedCompanies().stream()
                .map(ic -> ic.getCompany().getName())
                .collect(Collectors.toSet())
                : Set.of()
        );
        game.setRating(igdbGame.getRating() != null ? igdbGame.getRating() : 0.0);
        game.setLikes(0);

        return Optional.of(gameRepository.save(game));
    }

    private List<IgdbGameResponse> fetchGames(String baseBody, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int offset = safePage * safeSize;

        String body = """
                %s
                limit %d;
                offset %d;
                """.formatted(baseBody, safeSize, offset);

        String token = authService.getAccessToken();

        List<IgdbGameResponse> responses = webClient.post()
                .uri("/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(IgdbGameResponse.class)
                .collectList()
                .block();

        return responses != null ? responses : Collections.emptyList();
    }

    private Set<String> extractKeywords(IgdbGameResponse igdbGame) {
        if (igdbGame.getKeywords() == null) {
            return Set.of();
        }

        return igdbGame.getKeywords().stream()
                .map(KeywordResponse::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toSet());
    }
}
