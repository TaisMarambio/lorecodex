package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipationRepository extends JpaRepository<ChallengeParticipation, Long> {
    Optional<ChallengeParticipation> findByUserAndChallenge(User user, Challenge challenge);

    List<ChallengeParticipation> findByUser(User user);

    List<ChallengeParticipation> findByChallenge(Challenge challenge);

    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.user.id = :userId ORDER BY cp.joinedAt DESC")
    List<ChallengeParticipation> findByUserIdOrderByJoinedAtDesc(@Param("userId") Long userId);

    @Query("SELECT cp FROM ChallengeParticipation cp WHERE cp.challenge.id = :challengeId ORDER BY cp.joinedAt DESC")
    List<ChallengeParticipation> findByChallengeIdOrderByJoinedAtDesc(@Param("challengeId") Long challengeId);

    @Query("SELECT COUNT(cp) FROM ChallengeParticipation cp WHERE cp.challenge.id = :challengeId")
    Integer countByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT COUNT(cp) FROM ChallengeParticipation cp WHERE cp.challenge.id = :challengeId AND cp.completed = true")
    Integer countCompletedByChallengeId(@Param("challengeId") Long challengeId);

    void deleteByUserAndChallenge(User user, Challenge challenge);
}