package com.lorecodex.backend.controller;


import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;
import com.lorecodex.backend.mapper.ChallengeMapper;
import com.lorecodex.backend.model.Challenge;
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
        ChallengeResponse dto = service.createChallenge(user.getUsername(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
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

    //obtiene un challenge por id
    @GetMapping("/{id}")
    public ChallengeResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    //obtiene todos los challenges
    @GetMapping
    public List<ChallengeResponse> getAllChallenges(){
        return service.findAllChallenges();
    }

    //endpoints de participacion en challenges

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void join(@PathVariable Long id,
                     @AuthenticationPrincipal User user) {
        service.joinChallenge(id, user.getUsername());
    }

    //completar un item de un challenge
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

    //obtener challenges por titulo
    @GetMapping("/search")
    public ResponseEntity<List<ChallengeResponse>> searchChallenges(@RequestParam String title) {
        List<ChallengeResponse> challenges = service.findChallengesByTitle(title);
        return ResponseEntity.ok(challenges);
    }

    //quiero obtener el username del creador de un challenge
    @GetMapping("/{id}/author")
    public ResponseEntity<String> getAuthorUsername(@PathVariable Long id) {
        ChallengeResponse challenge = service.findById(id);
        if (challenge != null) {
            return ResponseEntity.ok(challenge.getCreatorUsername());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //quiero que mi usuario pueda "desunirse" de un challenge, que se elimine su participacion
    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveChallenge(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        service.leaveChallenge(id, user.getId());
    }

    //dificultad de un challenge
    @GetMapping("/{id}/difficulty")
    public ResponseEntity<String> getChallengeDifficulty(@PathVariable Long id) {
        String difficulty = service.getChallengeDifficulty(id);
        if (difficulty != null) {
            return ResponseEntity.ok(difficulty);
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