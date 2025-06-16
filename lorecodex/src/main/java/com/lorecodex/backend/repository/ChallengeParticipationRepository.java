package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.ChallengeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {
    boolean existsByChallenge_IdAndUser_Username(Long challengeId, String username);
    ChallengeParticipation findByChallenge_IdAndUser_Username(Long challengeId, String username);
    ChallengeParticipation findByChallenge_IdAndUser_Id(Long challengeId, Long userId);
}