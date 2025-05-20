package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.dto.response.igdb.GenreResponse;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.mapper.IgdbGameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.IgdbService;
import com.lorecodex.backend.service.TwitchAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    public String getTopGames() {
        String accessToken = authService.getAccessToken();

        String body = """
                fields name,genres.name,cover.url;
                sort rating desc;
                limit 10;
                """;

        return webClient.post()
                .uri("/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public List<IgdbGameResponse> searchGames(String query) {
        String token = authService.getAccessToken();

        String body = """
        search "%s";
        fields id, name, cover.url, genres.name, release_dates.date;
        limit 10;
    """.formatted(query);

        return webClient.post()
                .uri("/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(IgdbGameResponse.class)
                .collectList()
                .block();
    }

    @Override
    public Game importGameFromIgdb(CreateGameFromIgdbRequest request) {
        Optional<Game> existing = gameRepository.findByIgdbId(request.getIgdbId());
        if (existing.isPresent()) return existing.get();

        Game game = new Game();
        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setCoverImage(request.getCoverImage());
        game.setReleaseDate(request.getReleaseDate());
        game.setGenres(request.getGenres());
        game.setDevelopersAndPublishers(Set.of()); // vacío por ahora
        game.setRating(0.0);
        game.setLikes(0);

        return gameRepository.save(game);
    }

    @Override
    public Optional<IgdbGameResponse> getGameById(Long igdbId) {
        String token = authService.getAccessToken();

        String body = """
        fields id, name, summary, cover.url, genres.name, release_dates.date, involved_companies.company.name, rating;
        where id = %d;
    """.formatted(igdbId);

        List<IgdbGameResponse> result = webClient.post()
                .uri("/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(IgdbGameResponse.class)
                .collectList()
                .block();

        return result != null && !result.isEmpty() ? Optional.of(result.getFirst()) : Optional.empty();
    }

    @Override
    public Optional<Game> importGameById(Long igdbId) {
        Optional<IgdbGameResponse> optional = getGameById(igdbId);
        if (optional.isEmpty()) return Optional.empty();

        IgdbGameResponse igdbGame = optional.get();

        Game game = new Game();
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
        game.setReleaseDate(
                igdbGameMapper.getFirstReleaseDate(igdbGame)
        );
        game.setDevelopersAndPublishers(igdbGame.getInvolvedCompanies() != null
                ? igdbGame.getInvolvedCompanies().stream()
                .map(ic -> ic.getCompany().getName())
                .collect(Collectors.toSet())
                : Set.of()
        );
        game.setRating(igdbGame.getRating());
        game.setLikes(0);

        return Optional.of(gameRepository.save(game));
    }

    @Override
    public List<IgdbGameResponse> getTopGamesList() {
        String accessToken = authService.getAccessToken();

        String body = """
            fields id, name, cover.url, genres.name, release_dates.date, aggregated_rating;
            sort aggregated_rating desc;
            where cover != null;
            limit 10;
        """;

        return webClient.post()
                .uri("/games")
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(IgdbGameResponse.class)
                .collectList()
                .block();
    }
}