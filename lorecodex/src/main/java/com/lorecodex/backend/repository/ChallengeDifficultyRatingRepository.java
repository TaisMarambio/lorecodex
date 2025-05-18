package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeDifficultyRatingRepository extends JpaRepository<ChallengeDifficultyRating, Long> {
    Optional<ChallengeDifficultyRating> findByUserAndChallenge(User user, Challenge challenge);

    List<ChallengeDifficultyRating> findByUser(User user);

    List<ChallengeDifficultyRating> findByChallenge(Challenge challenge);

    @Query("SELECT AVG(r.difficultyLevel) FROM ChallengeDifficultyRating r WHERE r.challenge.id = :challengeId")
    Double findAverageDifficultyByChallengeId(@Param("challengeId") Long challengeId);

    void deleteByUserAndChallenge(User user, Challenge challenge);
}