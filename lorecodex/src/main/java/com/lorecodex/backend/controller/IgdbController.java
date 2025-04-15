package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.GameResponse;
import com.lorecodex.backend.service.IgdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/igdb")
public class IgdbController {

    private final IgdbService igdbService;

    public IgdbController(IgdbService igdbService) {
        this.igdbService = igdbService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameResponse>> searchGames(@RequestParam String query) {
        List<GameResponse> result = igdbService.searchGames(query);
        return ResponseEntity.ok(result);
    }

}
