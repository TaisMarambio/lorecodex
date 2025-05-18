package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.ChallengeParticipationRepository;
import com.lorecodex.backend.repository.ChallengeRepository;
import com.lorecodex.backend.repository.ChallengeDifficultyRatingRepository;
import com.lorecodex.backend.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeDifficultyRatingRepository difficultyRatingRepository;

    @Autowired
    public ChallengeServiceImpl(
            ChallengeRepository challengeRepository,
            ChallengeParticipationRepository participationRepository,
            ChallengeDifficultyRatingRepository difficultyRatingRepository) {
        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.difficultyRatingRepository = difficultyRatingRepository;
    }

    @Override
    @Transactional
    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    @Override
    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<Challenge> getChallengesByCreator(User creator) {
        return challengeRepository.findByCreator(creator);
    }

    @Override
    public List<Challenge> getChallengesByGame(Game game) {
        return challengeRepository.findByGame(game);
    }

    @Override
    @Transactional
    public void deleteChallenge(Long id, User requestingUser) throws IllegalAccessException {
        Optional<Challenge> challengeOpt = getChallengeById(id);

        if (challengeOpt.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found");
        }

        Challenge challenge = challengeOpt.get();

        // Check if user has permission to delete this challenge
        if (!canModify(challenge, requestingUser)) {
            throw new IllegalAccessException("You don't have permission to delete this challenge");
        }

        challengeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Challenge updateChallenge(Long id, Challenge updatedChallenge, User requestingUser) throws IllegalAccessException {
        Optional<Challenge> existingChallengeOpt = getChallengeById(id);

        if (existingChallengeOpt.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found");
        }

        Challenge existingChallenge = existingChallengeOpt.get();

        // Check if user has permission to update this challenge
        if (!canModify(existingChallenge, requestingUser)) {
            throw new IllegalAccessException("You don't have permission to update this challenge");
        }

        // Update the fields that are allowed to be modified
        existingChallenge.setTitle(updatedChallenge.getTitle());
        existingChallenge.setDescription(updatedChallenge.getDescription());

        return challengeRepository.save(existingChallenge);
    }

    @Override
    @Transactional
    public void updateChallengeStatistics(Long challengeId) {
        Optional<Challenge> challengeOpt = getChallengeById(challengeId);

        if (challengeOpt.isEmpty()) {
            return;
        }

        Challenge challenge = challengeOpt.get();

        // Update participant count
        Integer participantCount = participationRepository.countByChallengeId(challengeId);
        challenge.setParticipantCount(participantCount);

        // Update completion count
        Integer completionCount = participationRepository.countCompletedByChallengeId(challengeId);
        challenge.setCompletionCount(completionCount);

        // Update average difficulty
        Double averageDifficulty = difficultyRatingRepository.findAverageDifficultyByChallengeId(challengeId);
        challenge.setAverageDifficulty(averageDifficulty != null ? averageDifficulty : 0.0);

        // Save the updated challenge
        challengeRepository.save(challenge);
    }

    @Override
    public boolean canModify(Challenge challenge, User user) {
        // Check if user is the creator of the challenge or has admin role
        if (challenge.getCreator().getId().equals(user.getId())) {
            return true;
        }

        // Check if user has ROLE_ADMIN
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}