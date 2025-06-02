package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.ChallengeDifficultyRequest;
import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.ChallengeParticipationResponse;
import com.lorecodex.backend.dto.response.ChallengeResponse;
import com.lorecodex.backend.mapper.ChallengeMapper;
import com.lorecodex.backend.mapper.ChallengeParticipationMapper;
import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.ChallengeDifficultyRatingService;
import com.lorecodex.backend.service.ChallengeParticipationService;
import com.lorecodex.backend.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/challenges")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeParticipationService participationService;
    private final ChallengeDifficultyRatingService difficultyRatingService;
    private final GameRepository gameRepository;
    private final ChallengeMapper challengeMapper;
    private final ChallengeParticipationMapper participationMapper;

    @Autowired
    public ChallengeController(
            ChallengeService challengeService,
            ChallengeParticipationService participationService,
            ChallengeDifficultyRatingService difficultyRatingService,
            GameRepository gameRepository,
            ChallengeMapper challengeMapper,
            ChallengeParticipationMapper participationMapper) {
        this.challengeService = challengeService;
        this.participationService = participationService;
        this.difficultyRatingService = difficultyRatingService;
        this.gameRepository = gameRepository;
        this.challengeMapper = challengeMapper;
        this.participationMapper = participationMapper;
    }

    // Get all challenges
    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges(@AuthenticationPrincipal User user) {
        List<Challenge> challenges = challengeService.getAllChallenges();
        List<ChallengeResponse> response = challengeMapper.toDTOList(challenges, user);
        return ResponseEntity.ok(response);
    }

    // Get challenge by ID
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChallengeResponse response = challengeMapper.toDTO(challengeOpt.get(), user);
        return ResponseEntity.ok(response);
    }

    // Get challenges by game
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ChallengeResponse>> getChallengesByGame(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);

        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Challenge> challenges = challengeService.getChallengesByGame(gameOpt.get());
        List<ChallengeResponse> response = challengeMapper.toDTOList(challenges, user);
        return ResponseEntity.ok(response);
    }

    // Get challenges created by current user
    @GetMapping("/my-created")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChallengeResponse>> getMyChallenges(@AuthenticationPrincipal User user) {
        List<Challenge> challenges = challengeService.getChallengesByCreator(user);
        List<ChallengeResponse> response = challengeMapper.toDTOList(challenges, user);
        return ResponseEntity.ok(response);
    }

    // Get challenges where current user is participating
    @GetMapping("/my-participated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChallengeParticipationResponse>> getMyParticipatedChallenges(
            @AuthenticationPrincipal User user) {
        List<ChallengeParticipation> participations = participationService.getParticipationsByUser(user);
        List<ChallengeParticipationResponse> response = participationMapper.toDTOList(participations);
        return ResponseEntity.ok(response);
    }

    // Create a new challenge
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeResponse> createChallenge(
            @Valid @RequestBody ChallengeRequest request,
            @AuthenticationPrincipal User user) {

        Optional<Game> gameOpt = gameRepository.findById(request.getGameId());

        if (gameOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Challenge challenge = challengeMapper.toEntity(request, user, gameOpt.get());
        Challenge createdChallenge = challengeService.createChallenge(challenge);

        ChallengeResponse response = challengeMapper.toDTO(createdChallenge, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a challenge
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeResponse> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeRequest request,
            @AuthenticationPrincipal User user) {

        try {
            Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

            if (challengeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Challenge existingChallenge = challengeOpt.get();

            // Only update title and description, keep other properties the same
            existingChallenge.setTitle(request.getTitle());
            existingChallenge.setDescription(request.getDescription());

            Challenge updatedChallenge = challengeService.updateChallenge(id, existingChallenge, user);
            ChallengeResponse response = challengeMapper.toDTO(updatedChallenge, user);
            return ResponseEntity.ok(response);
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Delete a challenge
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        try {
            challengeService.deleteChallenge(id, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Join a challenge
    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeParticipationResponse> joinChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChallengeParticipation participation = participationService.joinChallenge(user, challengeOpt.get());
        ChallengeParticipationResponse response = participationMapper.toDTO(participation);
        return ResponseEntity.ok(response);
    }

    // Mark a challenge as completed
    @PostMapping("/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChallengeParticipationResponse> completeChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChallengeParticipation participation = participationService.completeChallenge(user, challengeOpt.get());
        ChallengeParticipationResponse response = participationMapper.toDTO(participation);
        return ResponseEntity.ok(response);
    }

    // Leave a challenge
    @DeleteMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> leaveChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        participationService.leaveChallenge(user, challengeOpt.get());
        return ResponseEntity.noContent().build();
    }

    // Rate challenge difficulty
    @PostMapping("/{id}/rate-difficulty")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rateDifficulty(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeDifficultyRequest request,
            @AuthenticationPrincipal User user) {

        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChallengeDifficultyRating rating = difficultyRatingService.rateOrUpdateDifficulty(
                user, challengeOpt.get(), request.getDifficultyLevel());

        // Trigger update of challenge statistics
        challengeService.updateChallengeStatistics(id);

        return ResponseEntity.ok().build();
    }

    // Get participants of a challenge
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ChallengeParticipationResponse>> getChallengeParticipants(@PathVariable Long id) {
        Optional<Challenge> challengeOpt = challengeService.getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ChallengeParticipation> participations = participationService.getParticipationsByChallenge(challengeOpt.get());
        List<ChallengeParticipationResponse> response = participationMapper.toDTOList(participations);
        return ResponseEntity.ok(response);
    }

    // Get average difficulty of a challenge
    @GetMapping("/{id}/average-difficulty")
    public ResponseEntity<Double> getAverageDifficulty(@PathVariable Long id) {
        Integer averageDifficulty = difficultyRatingService.getAverageDifficultyByChallengeId(id);
        return ResponseEntity.ok(averageDifficulty != null ? averageDifficulty : 0.0);
    }
}