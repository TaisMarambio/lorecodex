package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.ChallengeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {
    boolean existsByChallenge_IdAndUser_Username(Long challengeId, String username);
    ChallengeParticipation findByChallenge_IdAndUser_Username(Long challengeId, String username);
}