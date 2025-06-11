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

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;

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
}