package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;

    @PostMapping
    public ResponseEntity<ChallengeResponse> create(@RequestBody ChallengeRequest request,
                                                    @AuthenticationPrincipal User user) {
        ChallengeResponse created = service.createChallenge(user.getId(), request);
        // Devolver Location apuntando al recurso recién creado
        return ResponseEntity.created(URI.create("/challenges/" + created.getId())).body(created);
    }

    @GetMapping("/me/created")
    public List<ChallengeResponse> myCreated(@AuthenticationPrincipal User user) {
        return service.findChallengesCreatedByUser(user.getId());
    }

    @GetMapping("/me/joined")
    public List<ChallengeResponse> myJoined(@AuthenticationPrincipal User user) {
        return service.findChallengesJoinedByUser(user.getId());
    }

    @PutMapping("/{id}")
    public ChallengeResponse update(@PathVariable Long id,
                                    @RequestBody ChallengeRequest request,
                                    @AuthenticationPrincipal User user) {
        return service.updateChallenge(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User user) {
        service.deleteChallenge(id, user.getId());
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
        service.joinChallenge(id, user.getId());
    }

    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable Long id,
                      @AuthenticationPrincipal User user) {
        service.leaveChallenge(id, user.getId());
    }

    @GetMapping("/{id}/joined")
    public ResponseEntity<Boolean> isJoined(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) {
        boolean joined = service.isJoined(id, user.getId());
        return ResponseEntity.ok(joined);
    }

    @PostMapping("/{id}/items/{itemId}/complete")
    public ResponseEntity<ChallengeProgressDto> completeItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user
    ) {
        ChallengeProgressDto dto = service.completeItem(id, itemId, user.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/progress")
    public ChallengeProgressDto getProgress(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) {
        return service.getChallengeProgress(id, user.getId());
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
        ChallengeProgressDto dto = service.uncompleteItem(id, itemId, user.getId());
        return ResponseEntity.ok(dto);
    }
}
