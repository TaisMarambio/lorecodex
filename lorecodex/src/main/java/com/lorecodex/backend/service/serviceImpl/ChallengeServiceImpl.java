package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.request.ChallengeDifficultyRequest;
import com.lorecodex.backend.dto.response.ChallengeResponse;
import com.lorecodex.backend.model.*;
import com.lorecodex.backend.repository.ChallengeDifficultyRatingRepository;
import com.lorecodex.backend.repository.ChallengeParticipantRepository;
import com.lorecodex.backend.repository.ChallengeRepository;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.service.ChallengeService;
import com.lorecodex.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final ChallengeDifficultyRatingRepository difficultyRatingRepository;
    private final UserService userService;
    private final GameRepository gameRepository;

    @Autowired
    public ChallengeServiceImpl(
            ChallengeRepository challengeRepository,
            ChallengeParticipantRepository participantRepository,
            ChallengeDifficultyRatingRepository difficultyRatingRepository,
            UserService userService,
            GameRepository gameRepository) {
        this.challengeRepository = challengeRepository;
        this.participantRepository = participantRepository;
        this.difficultyRatingRepository = difficultyRatingRepository;
        this.userService = userService;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Challenge createChallenge(ChallengeRequest challengeRequest, Long userId) {
        User creator = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Game game = gameRepository.findById(challengeRequest.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Challenge challenge = Challenge.builder()
                .title(challengeRequest.getTitle())
                .description(challengeRequest.getDescription())
                .creator(creator)
                .game(game)
                .participantsCount(0)
                .completedCount(0)
                .averageDifficulty(0.0)
                .build();

        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
    }

    @Override
    @Transactional
    public void deleteChallenge(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId);
        challengeRepository.delete(challenge);
    }

    @Override
    public Page<Challenge> getAllChallenges(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }

    @Override
    public Page<Challenge> searchChallenges(String searchTerm, Pageable pageable) {
        return challengeRepository.searchChallenges(searchTerm, pageable);
    }

    @Override
    public List<Challenge> getChallengesByGameId(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        return challengeRepository.findByGame(game);
    }

    @Override
    public List<Challenge> getChallengesByCreatorId(Long userId) {
        User creator = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return challengeRepository.findByCreator(creator);
    }

    @Override
    @Transactional
    public Challenge joinChallenge(Long challengeId, Long userId) {
        Challenge challenge = getChallengeById(challengeId);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (participantRepository.existsByChallengeAndUser(challenge, user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already joined this challenge");
        }

        ChallengeParticipant participant = ChallengeParticipant.builder()
                .challenge(challenge)
                .user(user)
                .isCompleted(false)
                .build();

        participantRepository.save(participant);

        // Update participant count
        challenge.setParticipantsCount(participantRepository.countByChallenge(challenge));
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public Challenge completeChallenge(Long challengeId, Long userId) {
        Challenge challenge = getChallengeById(challengeId);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ChallengeParticipant participant = participantRepository.findByChallengeAndUser(challenge, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User must join the challenge before marking it as completed"));

        if (!participant.isCompleted()) {
            participant.setCompleted(true);
            participant.setCompletedAt(LocalDateTime.now());
            participantRepository.save(participant);

            // Update completed count
            challenge.setCompletedCount(participantRepository.countByChallengeAndIsCompleted(challenge, true));
            challengeRepository.save(challenge);
        }

        return challenge;
    }

    @Override
    @Transactional
    public Challenge rateDifficulty(Long challengeId, Long userId, ChallengeDifficultyRequest difficultyRequest) {
        Challenge challenge = getChallengeById(challengeId);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if user has already rated
        Optional<ChallengeDifficultyRating> existingRating =
                difficultyRatingRepository.findByChallengeAndUser(challenge, user);

        ChallengeDifficultyRating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setDifficultyLevel(difficultyRequest.getDifficultyLevel());
            rating.setRatedAt(LocalDateTime.now());
        } else {
            rating = ChallengeDifficultyRating.builder()
                    .challenge(challenge)
                    .user(user)
                    .difficultyLevel(difficultyRequest.getDifficultyLevel())
                    .build();
        }

        difficultyRatingRepository.save(rating);

        // Update average difficulty
        Double avgDifficulty = difficultyRatingRepository.getAverageDifficultyForChallenge(challenge);
        challenge.setAverageDifficulty(avgDifficulty != null ? avgDifficulty : 0.0);

        return challengeRepository.save(challenge);
    }

    @Override
    public ChallengeResponse toChallengeResponse(Challenge challenge, Long currentUserId) {
        boolean userJoined = false;
        boolean userCompleted = false;
        Integer userDifficultyRating = null;

        if (currentUserId != null) {
            User currentUser = userService.getUserById(currentUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Check if user has joined/completed
            Optional<ChallengeParticipant> participant =
                    participantRepository.findByChallengeAndUser(challenge, currentUser);

            if (participant.isPresent()) {
                userJoined = true;
                userCompleted = participant.get().isCompleted();
            }

            // Get user's difficulty rating if any
            Optional<ChallengeDifficultyRating> rating =
                    difficultyRatingRepository.findByChallengeAndUser(challenge, currentUser);

            if (rating.isPresent()) {
                userDifficultyRating = rating.get().getDifficultyLevel();
            }
        }

        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .creatorUsername(challenge.getCreator().getUsername())
                .creatorId(challenge.getCreator().getId())
                .gameName(challenge.getGame().getTitle())
                .gameId(challenge.getGame().getId())
                .gameCoverImage(challenge.getGame().getCoverImage())
                .createdAt(challenge.getCreatedAt())
                .participantsCount(challenge.getParticipantsCount())
                .completedCount(challenge.getCompletedCount())
                .averageDifficulty(challenge.getAverageDifficulty())
                .userJoined(userJoined)
                .userCompleted(userCompleted)
                .userDifficultyRating(userDifficultyRating)
                .build();
    }
}