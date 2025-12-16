package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.ChallengeRequest;
import com.lorecodex.backend.dto.response.challenge.ChallengeProgressDto;
import com.lorecodex.backend.dto.response.challenge.ChallengeResponse;
import com.lorecodex.backend.mapper.ChallengeMapper;
import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.ChallengeItem;
import com.lorecodex.backend.model.ChallengeParticipation;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.ChallengeParticipationRepository;
import com.lorecodex.backend.repository.ChallengeRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.ChallengeService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChallengeMapper mapper;

    @Override
    public ChallengeResponse createChallenge(Long creatorUserId, ChallengeRequest request) {
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Challenge challenge = mapper.toEntity(request, creator);
        challengeRepository.save(challenge);
        return mapper.toDto(challenge);
    }

    @Override
    public void joinChallenge(Long challengeId, Long userId) {
        if (participationRepository.existsByChallenge_IdAndUser_Id(challengeId, userId)) {
            return;
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        ChallengeParticipation participation = ChallengeParticipation.builder()
                .challenge(challenge)
                .user(user)
                .completedItems(new HashSet<>())
                .joinedAt(LocalDateTime.now())
                .build();

        participationRepository.save(participation);
    }

    @Override
    public void leaveChallenge(Long challengeId, Long userId) {
        participationRepository.deleteByChallenge_IdAndUser_Id(challengeId, userId);
    }

    @Override
    @Transactional
    public ChallengeProgressDto completeItem(Long challengeId, Long itemId, Long userId) {
        ChallengeParticipation participation =
                participationRepository.findByChallenge_IdAndUser_Id(challengeId, userId);

        if (participation == null) {
            throw new IllegalStateException("User has not joined this challenge");
        }

        ChallengeItem item = participation.getChallenge()
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not found in this challenge"));

        participation.getCompletedItems().add(item);

        if (participation.getCompletedItems().size() ==
                participation.getChallenge().getItems().size()) {
            participation.setCompletedAt(LocalDateTime.now());
        } else {
            participation.setCompletedAt(null);
        }

        participationRepository.save(participation);
        return mapper.toProgressDto(participation);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeResponse getChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));
        return mapper.toDto(challenge);
    }

    @Override
    public ChallengeResponse findById(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));
        return mapper.toDto(challenge);
    }

    @Override
    public List<ChallengeResponse> findAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ChallengeResponse updateChallenge(Long challengeId, ChallengeRequest request, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (!challenge.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("Only the creator can update the challenge");
        }

        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.getItems().clear();

        for (int i = 0; i < request.getItems().size(); i++) {
            ChallengeItem it = ChallengeItem.builder()
                    .description(request.getItems().get(i))
                    .orderPosition(i + 1)
                    .challenge(challenge)
                    .build();
            challenge.getItems().add(it);
        }

        return mapper.toDto(challenge);
    }

    @Override
    public void deleteChallenge(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (!isAdmin && !challenge.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("Only the creator can delete the challenge");
        }

        participationRepository.deleteByChallenge_Id(challengeId);
        challengeRepository.delete(challenge);
    }

    @Override
    public ChallengeProgressDto getChallengeProgress(Long challengeId, Long userId) {
        ChallengeParticipation participation =
                participationRepository.findByChallenge_IdAndUser_Id(challengeId, userId);
        if (participation == null) {
            throw new EntityNotFoundException("User has not joined this challenge");
        }
        return mapper.toProgressDto(participation);
    }

    @Override
    public List<ChallengeResponse> findChallengesByTitle(String title) {
        List<Challenge> challenges = challengeRepository.findByTitleContainingIgnoreCase(title);
        return challenges.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChallengeProgressDto uncompleteItem(Long challengeId, Long itemId, Long userId) {
        ChallengeParticipation participation =
                participationRepository.findByChallenge_IdAndUser_Id(challengeId, userId);

        if (participation == null) {
            throw new IllegalStateException("User has not joined this challenge");
        }

        boolean removed = participation.getCompletedItems()
                .removeIf(ci -> ci.getId().equals(itemId));

        if (!removed) {
            throw new IllegalStateException("El ítem no estaba completado previamente");
        }

        if (participation.getCompletedItems().size() <
                participation.getChallenge().getItems().size()) {
            participation.setCompletedAt(null);
        }

        participationRepository.save(participation);
        return mapper.toProgressDto(participation);
    }

    @Override
    public boolean isJoined(Long challengeId, Long userId) {
        return participationRepository.existsByChallenge_IdAndUser_Id(challengeId, userId);
    }

    @Override
    public List<ChallengeResponse> findChallengesCreatedByUser(Long userId) {
        List<Challenge> challenges = challengeRepository.findByCreator_Id(userId);
        return challenges.stream().map(mapper::toDto).toList();
    }

    @Override
    public List<ChallengeResponse> findChallengesJoinedByUser(Long userId) {
        List<ChallengeParticipation> parts = participationRepository.findByUser_Id(userId);
        // Distintos por id preservando orden de aparición
        Map<Long, Challenge> byId = new LinkedHashMap<>();
        for (ChallengeParticipation p : parts) {
            Challenge ch = p.getChallenge();
            if (ch != null && ch.getId() != null) {
                byId.putIfAbsent(ch.getId(), ch);
            }
        }
        return byId.values().stream().map(mapper::toDto).toList();
    }
}
