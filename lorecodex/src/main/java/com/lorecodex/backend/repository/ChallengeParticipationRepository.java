package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.ChallengeParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {
    boolean existsByChallenge_IdAndUser_Username(Long challengeId, String username);
    ChallengeParticipation findByChallenge_IdAndUser_Username(Long challengeId, String username);

    // Métodos por userId
    boolean existsByChallenge_IdAndUser_Id(Long challengeId, Long userId);
    ChallengeParticipation findByChallenge_IdAndUser_Id(Long challengeId, Long userId);

    // Listados
    List<ChallengeParticipation> findByUser_Id(Long userId);

    // Borrados
    void deleteByChallenge_IdAndUser_Id(Long challengeId, Long userId);
    void deleteByChallenge_Id(Long challengeId);
}