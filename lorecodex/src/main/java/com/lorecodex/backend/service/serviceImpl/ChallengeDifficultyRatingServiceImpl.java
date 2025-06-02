package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.ChallengeDifficultyRatingRepository;
import com.lorecodex.backend.service.ChallengeDifficultyRatingService;
import com.lorecodex.backend.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeDifficultyRatingServiceImpl implements ChallengeDifficultyRatingService {

    private final ChallengeDifficultyRatingRepository ratingRepository;
    private final ChallengeService challengeService;

    @Autowired
    public ChallengeDifficultyRatingServiceImpl(
            ChallengeDifficultyRatingRepository ratingRepository,
            ChallengeService challengeService) {
        this.ratingRepository = ratingRepository;
        this.challengeService = challengeService;
    }

    @Override
    public Optional<ChallengeDifficultyRating> getRating(User user, Challenge challenge) {
        return ratingRepository.findByUserAndChallenge(user, challenge);
    }

    @Override
    public List<ChallengeDifficultyRating> getRatingsByUser(User user) {
        return ratingRepository.findByUser(user);
    }

    @Override
    public List<ChallengeDifficultyRating> getRatingsByChallenge(Challenge challenge) {
        return ratingRepository.findByChallenge(challenge);
    }

    @Override
    @Transactional
    public ChallengeDifficultyRating rateOrUpdateDifficulty(User user, Challenge challenge, Integer difficultyLevel) {
        // Verify difficultyLevel is within allowed range (1-6)
        if (difficultyLevel < 1 || difficultyLevel > 6) {
            throw new IllegalArgumentException("Difficulty level must be between 1 and 6");
        }

        Optional<ChallengeDifficultyRating> existingRatingOpt = getRating(user, challenge);

        ChallengeDifficultyRating rating;
        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setDifficultyLevel(difficultyLevel);
        } else {
            rating = new ChallengeDifficultyRating();
            rating.setUser(user);
            rating.setChallenge(challenge);
            rating.setDifficultyLevel(difficultyLevel);
        }

        ChallengeDifficultyRating savedRating = ratingRepository.save(rating);

        // Update challenge statistics
        challengeService.updateChallengeStatistics(challenge.getId());

        return savedRating;
    }

    @Override
    @Transactional
    public void deleteRating(User user, Challenge challenge) {
        ratingRepository.deleteByUserAndChallenge(user, challenge);

        // Update challenge statistics
        challengeService.updateChallengeStatistics(challenge.getId());
    }

    @Override
    public Integer getAverageDifficultyByChallengeId(Long challengeId) {
        return ratingRepository.findAverageDifficultyByChallengeId(challengeId);
    }
}