package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;

import java.util.List;

public interface ChallengeService {

    ChallengeResponse createChallenge(Long creatorUserId, ChallengeRequest request);

    void joinChallenge(Long challengeId, Long userId);

    void leaveChallenge(Long challengeId, Long userId);

    ChallengeProgressDto completeItem(Long challengeId, Long itemId, Long userId);

    ChallengeResponse getChallenge(Long challengeId, Long userId);

    ChallengeResponse findById(Long challengeId);

    List<ChallengeResponse> findAllChallenges();

    ChallengeResponse updateChallenge(Long challengeId, ChallengeRequest request, Long userId);

    void deleteChallenge(Long challengeId, Long userId);

    ChallengeProgressDto getChallengeProgress(Long challengeId, Long userId);

    List<ChallengeResponse> findChallengesByTitle(String title);

    boolean isJoined(Long challengeId, Long userId);

    ChallengeProgressDto uncompleteItem(Long challengeId, Long itemId, Long userId);

    // Nuevos listados
    List<ChallengeResponse> findChallengesCreatedByUser(Long userId);
    List<ChallengeResponse> findChallengesJoinedByUser(Long userId);
}
