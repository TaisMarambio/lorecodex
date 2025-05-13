package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.request.ChallengeDifficultyRequest;
import com.lorecodex.backend.dto.response.ChallengeResponse;
import com.lorecodex.backend.model.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChallengeService {

    Challenge createChallenge(ChallengeRequest challengeRequest, Long userId);

    Challenge getChallengeById(Long challengeId);

    void deleteChallenge(Long challengeId);

    Page<Challenge> getAllChallenges(Pageable pageable);

    Page<Challenge> searchChallenges(String searchTerm, Pageable pageable);

    List<Challenge> getChallengesByGameId(Long gameId);

    List<Challenge> getChallengesByCreatorId(Long userId);

    Challenge joinChallenge(Long challengeId, Long userId);

    Challenge completeChallenge(Long challengeId, Long userId);

    Challenge rateDifficulty(Long challengeId, Long userId, ChallengeDifficultyRequest difficultyRequest);

    ChallengeResponse toChallengeResponse(Challenge challenge, Long currentUserId);
}