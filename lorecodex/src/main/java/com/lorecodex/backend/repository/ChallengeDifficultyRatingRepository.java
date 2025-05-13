package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeDifficultyRatingRepository extends JpaRepository<ChallengeDifficultyRating, Long> {
    Optional<ChallengeDifficultyRating> findByChallengeAndUser(Challenge challenge, User user);

    @Query("SELECT AVG(r.difficultyLevel) FROM ChallengeDifficultyRating r WHERE r.challenge = :challenge")
    Double getAverageDifficultyForChallenge(Challenge challenge);
}