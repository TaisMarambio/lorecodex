package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeParticipant;
import com.lorecodex.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    List<ChallengeParticipant> findByChallenge(Challenge challenge);
    List<ChallengeParticipant> findByUser(User user);
    Optional<ChallengeParticipant> findByChallengeAndUser(Challenge challenge, User user);
    boolean existsByChallengeAndUser(Challenge challenge, User user);
    int countByChallenge(Challenge challenge);
    int countByChallengeAndIsCompleted(Challenge challenge, boolean isCompleted);
}