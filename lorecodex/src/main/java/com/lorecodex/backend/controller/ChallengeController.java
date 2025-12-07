package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;
import com.lorecodex.backend.mapper.ChallengeMapper;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;
    private final ChallengeMapper challengeMapper;

    @PostMapping
    public ResponseEntity<ChallengeResponse> create(@RequestBody ChallengeRequest request,
                                                    @AuthenticationPrincipal User user) {
        service.createChallenge(user.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ChallengeResponse update(@PathVariable Long id,
                                    @RequestBody ChallengeRequest request,
                                    @AuthenticationPrincipal User user) {
        return service.updateChallenge(id, request, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User user) {
        service.deleteChallenge(id, user.getUsername());
    }

    @GetMapping("/{id}")
    public ChallengeResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    // Endpoint con soporte de paginación opcional
    @GetMapping
    public List<ChallengeResponse> getAllChallenges(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size) {

        // Si page y size están en sus valores por defecto, devolver todos
        // Esto mantiene compatibilidad con el frontend existente
        if (page == 0 && size == 12) {
            return service.findAllChallenges();
        }

        // Implementación simple de paginación manual
        List<ChallengeResponse> allChallenges = service.findAllChallenges();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allChallenges.size());

        if (fromIndex >= allChallenges.size()) {
            return List.of();
        }

        return allChallenges.subList(fromIndex, toIndex);
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void join(@PathVariable Long id,
                     @AuthenticationPrincipal User user) {
        service.joinChallenge(id, user.getUsername());
    }

    @PostMapping("/{id}/items/{itemId}/complete")
    public ResponseEntity<ChallengeProgressDto> completeItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user
    ) {
        ChallengeProgressDto dto = service.completeItem(id, itemId, user.getUsername());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/progress")
    public ChallengeProgressDto getProgress(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) {
        return service.getChallengeProgress(id, user.getUsername());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChallengeResponse>> searchChallenges(
            @RequestParam String title,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size) {

        List<ChallengeResponse> allResults = service.findChallengesByTitle(title);

        // Paginación manual
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allResults.size());

        if (fromIndex >= allResults.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(allResults.subList(fromIndex, toIndex));
    }

    @GetMapping("/{id}/author")
    public ResponseEntity<String> getAuthorUsername(@PathVariable Long id) {
        ChallengeResponse challenge = service.findById(id);
        if (challenge != null) {
            return ResponseEntity.ok(challenge.getCreatorUsername());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/items/{itemId}/uncomplete")
    public ResponseEntity<ChallengeProgressDto> uncompleteItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user
    ) {
        ChallengeProgressDto dto = service.uncompleteItem(id, itemId, user.getUsername());
        return ResponseEntity.ok(dto);
    }
}
