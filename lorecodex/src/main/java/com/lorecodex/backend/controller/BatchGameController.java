package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.BatchGameRequest;
import com.lorecodex.backend.dto.response.BatchGameResponse;
import com.lorecodex.backend.service.BatchGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/batch")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BatchGameController {

    private final BatchGameService batchGameService;

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatchGameResponse> importGamesInBatch(@RequestBody BatchGameRequest request) {
        if (request.getGames() == null || request.getGames().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BatchGameResponse response = batchGameService.importGamesInBatch(request);
        return ResponseEntity.ok(response);
    }
}