package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.ListItemResponse;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.model.ListItem;
import com.lorecodex.backend.model.UserList;

import com.lorecodex.backend.repository.ChallengeRepository;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserListMapper {
    private final GameRepository gameRepository;
    private final GuideRepository guideRepository;
    private final ChallengeRepository challengeRepository;

    /**
     * Convierte un UserList a UserListResponse.
     *
     * @param list El UserList a convertir.
     * @return UserListResponse con los datos del UserList.
     */

    public UserListResponse toResponse(UserList list) {
        List<ListItemResponse> itemDtos = list.getItems().stream()
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .id(list.getId())
                .title(list.getTitle())
                .description(list.getDescription())
                .createdAt(list.getCreatedAt())
                .userId(list.getUser().getId())
                .username(list.getUser().getUsername())
                .items(itemDtos)
                .build();
    }

    public ListItemResponse toItemResponse(ListItem item) {
        ListItemResponse dto = new ListItemResponse();
        dto.setId(item.getId());
        dto.setType(item.getType());
        dto.setReferenceId(item.getReferenceId());
        dto.setPosition(item.getPosition());

        // Cargamos y enriquecemos según tipo
        switch (item.getType()) {
            case GAME -> {
                gameRepository.findById(item.getReferenceId()).ifPresent(g -> {
                    dto.setTitle(g.getTitle());
                    dto.setThumbnailUrl(g.getCoverImage());
                });
            }
            case GUIDE -> {
                guideRepository.findById(item.getReferenceId()).ifPresent(gu -> {
                    dto.setTitle(gu.getTitle());
                    dto.setThumbnailUrl(gu.getCoverImageUrl());
                });
            }
            case CHALLENGE -> {
                challengeRepository.findById(item.getReferenceId()).ifPresent(ch -> {
                    dto.setTitle(ch.getTitle());
                    // si tienes alguna imagen para challenges, úsala; si no, puedes dejar null
                });
            }
        }
        return dto;
    }
}

