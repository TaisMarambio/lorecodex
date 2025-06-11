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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChallengeMapper mapper;

    @Override
    public void createChallenge(String creatorUsername, ChallengeRequest request) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Challenge challenge = mapper.toEntity(request, creator);
        challengeRepository.save(challenge);
    }

    @Override
    public void joinChallenge(Long challengeId, String username) {
        if (participationRepository.existsByChallenge_IdAndUser_Username(challengeId, username)) {
            return; // already joined
        }
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ChallengeParticipation participation = ChallengeParticipation.builder()
                .challenge(challenge)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();
        participationRepository.save(participation);
    }

    @Override
    @Transactional
    public ChallengeProgressDto completeItem(Long challengeId,
                                             Long itemId,
                                             String username) {
        // 1) Cargo la participación
        ChallengeParticipation participation = participationRepository
                .findByChallenge_IdAndUser_Username(challengeId, username);
        if (participation == null) {
            throw new IllegalStateException("User has not joined this challenge");
        }

        // 2) Localizo el ítem dentro del challenge
        ChallengeItem item = participation.getChallenge().getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item not found in this challenge"));

        // 3) Intento añadir al set de completados
        boolean wasAdded = participation.getCompletedItems().add(item);
        if (!wasAdded) {
            // opcional: lanzar excepción o simplemente ignorar si ya estaba
            // throw new IllegalStateException("Item already completed");
        }

        // 4) Calculo progreso
        int completed = participation.getCompletedItems().size();
        int total     = participation.getChallenge().getItems().size();
        double progress = 100.0 * completed / total;

        // 5) Marco completion timestamp si corresponde
        if (completed == total) {
            participation.setCompletedAt(LocalDateTime.now());
        } else {
            participation.setCompletedAt(null);
        }

        // 6) Persisto cambios
        participationRepository.save(participation);

        // 7) Devuelvo DTO
        return ChallengeProgressDto.builder()
                .challengeId(challengeId)
                .completed(completed)
                .total(total)
                .progress(progress)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeResponse getChallenge(Long challengeId, String username) {
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
    public ChallengeResponse updateChallenge(Long challengeId, ChallengeRequest request, String username) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (!challenge.getCreator().getUsername().equals(username)) {
            throw new IllegalStateException("Only the creator can update the challenge");
        }

        // 1) Actualizo título y descripción
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());

        // 2) Limpio la lista existente (Hibernate entiende el orphanRemoval)
        challenge.getItems().clear();

        // 3) Creo y agrego los nuevos items, manteniendo orden (1-based)
        for (int i = 0; i < request.getItems().size(); i++) {
            ChallengeItem it = ChallengeItem.builder()
                    .description(request.getItems().get(i))
                    .orderPosition(i + 1)
                    .challenge(challenge)
                    .build();
            challenge.getItems().add(it);
        }

        // 4) Guardo (Merge implícito por @Transactional)
        //    No necesitas usar save() si estás dentro de una transacción y challenge ya está gestionada.
        //challengeRepository.save(challenge);

        return mapper.toDto(challenge);
    }

    @Override
    public void deleteChallenge(Long challengeId, String username) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        if (!challenge.getCreator().getUsername().equals(username)) {
            throw new IllegalStateException("Only the creator can delete the challenge");
        }

        // Delete all participations related to this challenge
        participationRepository.deleteById(challengeId);
        // Delete the challenge itself
        challengeRepository.delete(challenge);
    }

    @Override
    public ChallengeProgressDto getChallengeProgress(Long challengeId, String username) {
        ChallengeParticipation participation = participationRepository.findByChallenge_IdAndUser_Username(challengeId, username);
        if (participation == null) {
            throw new EntityNotFoundException("User has not joined this challenge");
        }
        return mapper.toProgressDto(participation);
    }

}