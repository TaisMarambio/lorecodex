package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.GameDetailResponse;
import com.lorecodex.backend.dto.response.GameSearchResponse;
import com.lorecodex.backend.dto.response.PagedResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/igdb")
@RequiredArgsConstructor
public class IgdbController {
    private final IgdbService igdbService;
    private final IgdbGameMapper igdbGameMapper;
    private final GameMapper gameMapper;

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<GameSearchResponse>> searchGames(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") String page,
            @RequestParam(required = false, defaultValue = "12") String size
    ) {
        int safePage = parsePage(page, 0);
        int safeSize = Math.max(1, parsePage(size, 12));
        List<IgdbGameResponse> results = igdbService.searchGames(query, safePage, safeSize);
        List<GameSearchResponse> dto = results.stream()
                .map(igdbGameMapper::toSearchDto)
                .collect(Collectors.toList());
        boolean hasNext = results.size() == safeSize;
        return ResponseEntity.ok(PagedResponse.from(safePage, safeSize, dto, hasNext));
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
                .map(gameMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top")
    public ResponseEntity<PagedResponse<GameDetailResponse>> getTopGames(
            @RequestParam(required = false, defaultValue = "0") String page,
            @RequestParam(required = false, defaultValue = "12") String size
    ) {
        int safePage = parsePage(page, 0);
        int safeSize = Math.max(1, parsePage(size, 12));
        List<IgdbGameResponse> results = igdbService.getTopGames(safePage, safeSize);
        List<GameDetailResponse> dto = results.stream()
                .map(igdbGameMapper::toDetailDto)
                .collect(Collectors.toList());
        boolean hasNext = results.size() == safeSize;
        return ResponseEntity.ok(PagedResponse.from(safePage, safeSize, dto, hasNext));
    }

    @PostMapping("/import")
    public ResponseEntity<GameDetailResponse> importGame(@RequestBody CreateGameFromIgdbRequest request) {
        Game importedGame = igdbService.importGameFromIgdb(request);
        GameDetailResponse response = gameMapper.toDTO(importedGame);
        return ResponseEntity.ok(response);
    }

    private int parsePage(String raw, int fallback) {
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

}
