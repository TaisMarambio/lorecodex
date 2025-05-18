package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ChallengeDifficultyRatingService {
    Optional<ChallengeDifficultyRating> getRating(User user, Challenge challenge);

    List<ChallengeDifficultyRating> getRatingsByUser(User user);

    List<ChallengeDifficultyRating> getRatingsByChallenge(Challenge challenge);

    ChallengeDifficultyRating rateOrUpdateDifficulty(User user, Challenge challenge, Integer difficultyLevel);

    void deleteRating(User user, Challenge challenge);

    Double getAverageDifficultyByChallengeId(Long challengeId);
}