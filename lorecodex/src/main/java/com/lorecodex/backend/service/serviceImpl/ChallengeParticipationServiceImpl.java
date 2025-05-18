package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.ChallengeParticipationRepository;
import com.lorecodex.backend.service.ChallengeParticipationService;
import com.lorecodex.backend.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeParticipationServiceImpl implements ChallengeParticipationService {

    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeService challengeService;

    @Autowired
    public ChallengeParticipationServiceImpl(
            ChallengeParticipationRepository participationRepository,
            ChallengeService challengeService) {
        this.participationRepository = participationRepository;
        this.challengeService = challengeService;
    }

    @Override
    public Optional<ChallengeParticipation> getParticipation(User user, Challenge challenge) {
        return participationRepository.findByUserAndChallenge(user, challenge);
    }

    @Override
    public List<ChallengeParticipation> getParticipationsByUser(User user) {
        return participationRepository.findByUser(user);
    }

    @Override
    public List<ChallengeParticipation> getParticipationsByChallenge(Challenge challenge) {
        return participationRepository.findByChallenge(challenge);
    }

    @Override
    @Transactional
    public ChallengeParticipation joinChallenge(User user, Challenge challenge) {
        Optional<ChallengeParticipation> existingParticipationOpt = getParticipation(user, challenge);

        if (existingParticipationOpt.isPresent()) {
            return existingParticipationOpt.get(); // User is already part of the challenge
        }

        ChallengeParticipation participation = new ChallengeParticipation();
        participation.setUser(user);
        participation.setChallenge(challenge);
        participation.setJoinedAt(LocalDateTime.now());
        participation.setCompleted(false);

        ChallengeParticipation savedParticipation = participationRepository.save(participation);

        // Update challenge statistics
        challengeService.updateChallengeStatistics(challenge.getId());

        return savedParticipation;
    }

    @Override
    @Transactional
    public ChallengeParticipation completeChallenge(User user, Challenge challenge) {
        Optional<ChallengeParticipation> participationOpt = getParticipation(user, challenge);

        if (participationOpt.isEmpty()) {
            // If user hasn't joined the challenge, join them automatically and mark as completed
            ChallengeParticipation participation = new ChallengeParticipation();
            participation.setUser(user);
            participation.setChallenge(challenge);
            participation.setJoinedAt(LocalDateTime.now());
            participation.setCompleted(true);
            participation.setCompletedAt(LocalDateTime.now());

            ChallengeParticipation savedParticipation = participationRepository.save(participation);

            // Update challenge statistics
            challengeService.updateChallengeStatistics(challenge.getId());

            return savedParticipation;
        } else {
            ChallengeParticipation participation = participationOpt.get();

            // Only mark as completed if not already completed
            if (!participation.isCompleted()) {
                participation.setCompleted(true);
                participation.setCompletedAt(LocalDateTime.now());

                ChallengeParticipation updatedParticipation = participationRepository.save(participation);

                // Update challenge statistics
                challengeService.updateChallengeStatistics(challenge.getId());

                return updatedParticipation;
            }

            return participation;
        }
    }

    @Override
    @Transactional
    public void leaveChallenge(User user, Challenge challenge) {
        participationRepository.deleteByUserAndChallenge(user, challenge);

        // Update challenge statistics
        challengeService.updateChallengeStatistics(challenge.getId());
    }

    @Override
    public int countParticipantsByChallengeId(Long challengeId) {
        return participationRepository.countByChallengeId(challengeId);
    }

    @Override
    public int countCompletionsByChallengeId(Long challengeId) {
        return participationRepository.countCompletedByChallengeId(challengeId);
    }
}