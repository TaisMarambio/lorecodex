package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationService {
    Optional<ChallengeParticipation> getParticipation(User user, Challenge challenge);

    List<ChallengeParticipation> getParticipationsByUser(User user);

    List<ChallengeParticipation> getParticipationsByChallenge(Challenge challenge);

    ChallengeParticipation joinChallenge(User user, Challenge challenge);

    ChallengeParticipation completeChallenge(User user, Challenge challenge);

    void leaveChallenge(User user, Challenge challenge);

    int countParticipantsByChallengeId(Long challengeId);

    int countCompletionsByChallengeId(Long challengeId);
}