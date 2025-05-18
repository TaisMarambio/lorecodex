package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.ChallengeParticipationResponse;
import com.lorecodex.backend.model.ChallengeParticipation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChallengeParticipationMapper {

    public ChallengeParticipationResponse toDTO(ChallengeParticipation participation) {
        return ChallengeParticipationResponse.builder()
                .id(participation.getId())
                .userId(participation.getUser().getId())
                .username(participation.getUser().getUsername())
                .challengeId(participation.getChallenge().getId())
                .challengeTitle(participation.getChallenge().getTitle())
                .joinedAt(participation.getJoinedAt())
                .completed(participation.isCompleted())
                .completedAt(participation.getCompletedAt())
                .build();
    }

    public List<ChallengeParticipationResponse> toDTOList(List<ChallengeParticipation> participations) {
        return participations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}