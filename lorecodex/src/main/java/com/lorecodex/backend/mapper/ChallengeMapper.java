package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeItemDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;
import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeItem;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class ChallengeMapper {

    public Challenge toEntity(ChallengeRequest req, User creator) {
        Challenge challenge = Challenge.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .creator(creator)
                .build();

        List<ChallengeItem> items = new ArrayList<>();
        IntStream.range(0, req.getItems().size()).forEach(i ->
                items.add(ChallengeItem.builder()
                        .description(req.getItems().get(i))
                        .orderPosition(i)
                        .challenge(challenge)
                        .build()));
        challenge.setItems(items);
        return challenge;
    }

    public ChallengeResponse toDto(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .creatorUsername(challenge.getCreator().getUsername())
                .items(challenge.getItems().stream()
                        .map(item -> ChallengeItemDto.builder()
                                .id(item.getId())
                                .description(item.getDescription())
                                .order(item.getOrderPosition())
                                .build())
                        .toList())
                .build();
    }

    public ChallengeProgressDto toProgressDto(ChallengeParticipation participation) {
        int total = participation.getChallenge().getItems().size();
        int completed = participation.getCompletedItems().size();
        double progress = total == 0 ? 0 : (double) completed / total * 100.0;
        return ChallengeProgressDto.builder()
                .challengeId(participation.getChallenge().getId())
                .completed(completed)
                .total(total)
                .progress(progress)
                .build();
    }
}