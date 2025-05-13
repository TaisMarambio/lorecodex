package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.request.ChallengeDifficultyRequest;
import com.lorecodex.backend.dto.response.ChallengeResponse;
import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return ((com.lorecodex.backend.model.User) authentication.getPrincipal()).getId();
        }
        return null;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ChallengeResponse> createChallenge(@Valid @RequestBody ChallengeRequest challengeRequest) {
        Long userId = getCurrentUserId();
        Challenge challenge = challengeService.createChallenge(challengeRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challengeService.toChallengeResponse(challenge, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable("id") Long challengeId) {
        Long userId = getCurrentUserId();
        Challenge challenge = challengeService.getChallengeById(challengeId);
        return ResponseEntity.ok(challengeService.toChallengeResponse(challenge, userId));
    }

    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Long userId = getCurrentUserId();
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Challenge> challenges = challengeService.getAllChallenges(pageable);

        List<ChallengeResponse> responseList = challenges.getContent().stream()
                .map(challenge -> challengeService.toChallengeResponse(challenge, userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChallengeResponse>> searchChallenges(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> challenges = challengeService.searchChallenges(query, pageable);

        List<ChallengeResponse> responseList = challenges.getContent().stream()
                .map(challenge -> challengeService.toChallengeResponse(challenge, userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ChallengeResponse>> getChallengesByGame(@PathVariable Long gameId) {
        Long userId = getCurrentUserId();
        List<Challenge> challenges = challengeService.getChallengesByGameId(gameId);

        List<ChallengeResponse> responseList = challenges.stream()
                .map(challenge -> challengeService.toChallengeResponse(challenge, userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeResponse>> getChallengesByCreator(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        List<Challenge> challenges = challengeService.getChallengesByCreatorId(userId);

        List<ChallengeResponse> responseList = challenges.stream()
                .map(challenge -> challengeService.toChallengeResponse(challenge, currentUserId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ChallengeResponse> joinChallenge(@PathVariable("id") Long challengeId) {
        Long userId = getCurrentUserId();
        Challenge challenge = challengeService.joinChallenge(challengeId, userId);
        return ResponseEntity.ok(challengeService.toChallengeResponse(challenge, userId));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ChallengeResponse> completeChallenge(@PathVariable("id") Long challengeId) {
        Long userId = getCurrentUserId();
        Challenge challenge = challengeService.completeChallenge(challengeId, userId);
        return ResponseEntity.ok(challengeService.toChallengeResponse(challenge, userId));
    }

    @PostMapping("/{id}/rate")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ChallengeResponse> rateChallengeDifficulty(
            @PathVariable("id") Long challengeId,
            @Valid @RequestBody ChallengeDifficultyRequest difficultyRequest) {

        Long userId = getCurrentUserId();
        Challenge challenge = challengeService.rateDifficulty(challengeId, userId, difficultyRequest);
        return ResponseEntity.ok(challengeService.toChallengeResponse(challenge, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteChallenge(@PathVariable("id") Long challengeId) {
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.noContent().build();
    }
}