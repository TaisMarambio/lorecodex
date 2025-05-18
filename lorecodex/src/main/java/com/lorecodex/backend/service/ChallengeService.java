package com.lorecodex.backend.service;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);

    Optional<Challenge> getChallengeById(Long id);

    List<Challenge> getAllChallenges();

    List<Challenge> getChallengesByCreator(User creator);

    List<Challenge> getChallengesByGame(Game game);

    void deleteChallenge(Long id, User requestingUser) throws IllegalAccessException;

    Challenge updateChallenge(Long id, Challenge challenge, User requestingUser) throws IllegalAccessException;

    void updateChallengeStatistics(Long challengeId);

    boolean canModify(Challenge challenge, User user);
}