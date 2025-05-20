package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.ChallengeResponse;
import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeDifficultyRating;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChallengeMapper {

    public Challenge toEntity(ChallengeRequest dto, User creator, Game game) {
        Challenge challenge = new Challenge();
        challenge.setTitle(dto.getTitle());
        challenge.setDescription(dto.getDescription());
        challenge.setCreator(creator);
        challenge.setGame(game);
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setParticipantCount(0);
        challenge.setCompletionCount(0);
        challenge.setAverageDifficulty(0);
        return challenge;
    }

    public ChallengeResponse toDTO(Challenge challenge, User currentUser) {
        ChallengeResponse dto = new ChallengeResponse();
        dto.setId(challenge.getId());
        dto.setTitle(challenge.getTitle());
        dto.setDescription(challenge.getDescription());
        dto.setCreatorId(challenge.getCreator().getId());
        dto.setCreatorUsername(challenge.getCreator().getUsername());
        dto.setGameId(challenge.getGame().getId());
        dto.setGameTitle(challenge.getGame().getTitle());
        dto.setGameCoverImage(challenge.getGame().getCoverImage());
        dto.setCreatedAt(challenge.getCreatedAt());
        dto.setParticipantCount(challenge.getParticipantCount());
        dto.setCompletionCount(challenge.getCompletionCount());
        dto.setAverageDifficulty(challenge.getAverageDifficulty());

        // Si hay un usuario actual, verifica su participación y calificación
        if (currentUser != null) {
            Optional<ChallengeParticipation> participation = challenge.getParticipants().stream()
                    .filter(p -> p.getUser().getId().equals(currentUser.getId()))
                    .findFirst();

            dto.setUserParticipating(participation.isPresent());
            dto.setUserCompleted(participation.map(ChallengeParticipation::isCompleted).orElse(false));

            Optional<ChallengeDifficultyRating> rating = challenge.getDifficultyRatings().stream()
                    .filter(r -> r.getUser().getId().equals(currentUser.getId()))
                    .findFirst();

            dto.setUserDifficultyRating(rating.map(ChallengeDifficultyRating::getDifficultyLevel).orElse(null));
        } else {
            dto.setUserParticipating(false);
            dto.setUserCompleted(false);
            dto.setUserDifficultyRating(null);
        }

        return dto;
    }

    public List<ChallengeResponse> toDTOList(List<Challenge> challenges, User currentUser) {
        return challenges.stream()
                .map(challenge -> toDTO(challenge, currentUser))
                .collect(Collectors.toList());
    }

    // Sobrecarga para casos donde no hay usuario actual (endpoints públicos)
    public ChallengeResponse toDTO(Challenge challenge) {
        return toDTO(challenge, null);
    }

    public List<ChallengeResponse> toDTOList(List<Challenge> challenges) {
        return toDTOList(challenges, null);
    }
}