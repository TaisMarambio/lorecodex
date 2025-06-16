package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;

import java.util.List;

public interface ChallengeService {

    ChallengeResponse createChallenge(String creatorUsername, ChallengeRequest request);

    void joinChallenge(Long challengeId, String username);

    ChallengeProgressDto completeItem(Long challengeId, Long itemId, String username);

    ChallengeResponse getChallenge(Long challengeId, String username);

    ChallengeResponse findById(Long challengeId);

    List<ChallengeResponse> findAllChallenges();

    ChallengeResponse updateChallenge(Long challengeId, ChallengeRequest request, String username);

    void deleteChallenge(Long challengeId, String username);

    ChallengeProgressDto getChallengeProgress(Long challengeId, String username);

    //quiero buscar challenges por titulo
    List<ChallengeResponse> findChallengesByTitle(String title);

    //quiero desunirme de un challenge
    void leaveChallenge(Long challengeId, Long userId);

    //difficultad de un challenge
    String getChallengeDifficulty(Long challengeId);

    //quiero que el usuario que se unio al challenge pueda marcar como uncomplete un item
    ChallengeProgressDto uncompleteItem(Long challengeId, Long itemId, String username);

}