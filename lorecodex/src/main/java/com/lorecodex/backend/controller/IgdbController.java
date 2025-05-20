package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.dto.response.GameSearchResponse;
import com.lorecodex.backend.dto.response.igdb.CreateGameFromIgdbRequest;
import com.lorecodex.backend.dto.response.igdb.IgdbGameResponse;
import com.lorecodex.backend.mapper.GameMapper;
import com.lorecodex.backend.mapper.IgdbGameMapper;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.service.IgdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/igdb")
@RequiredArgsConstructor
public class IgdbController {
    private final IgdbService igdbService;
    private final IgdbGameMapper igdbGameMapper;
    private final GameMapper gameMapper;

    @GetMapping("/search")
    public ResponseEntity<List<GameSearchResponse>> searchGames(@RequestParam String query) {
        List<IgdbGameResponse> results = igdbService.searchGames(query);
        return ResponseEntity.ok(
                results.stream()
                        .map(igdbGameMapper::toSearchDto)
                        .toList()
        );
    }

    @GetMapping("/{igdbId}")
    public ResponseEntity<GameDetailResponse> getGameDetail(@PathVariable Long igdbId) {
        return igdbService.getGameById(igdbId)
                .map(igdbGameMapper::toDetailDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/import/{igdbId}")
    public ResponseEntity<GameDetailResponse> importGame(@PathVariable Long igdbId) {
        return igdbService.importGameById(igdbId)
                .map(gameMapper::toDTO) // asume que esto devuelve GameDetailResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top")
    public ResponseEntity<List<GameSearchResponse>> getTopGames() {
        List<IgdbGameResponse> topGames = igdbService.getTopGamesList();
        List<GameSearchResponse> response = topGames.stream()
                .map(igdbGameMapper::toSearchDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    //este es para chequear con un raw que funciona!
    @PostMapping("/import")
    public ResponseEntity<GameDetailResponse> importGame(@RequestBody CreateGameFromIgdbRequest request) {
        Game importedGame = igdbService.importGameFromIgdb(request);
        GameDetailResponse response = gameMapper.toDTO(importedGame);
        return ResponseEntity.ok(response);
    }

}
