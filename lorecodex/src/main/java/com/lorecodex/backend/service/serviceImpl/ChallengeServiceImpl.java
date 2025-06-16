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
import com.lorecodex.backend.model.ChallengeDifficulty;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
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
    public ChallengeResponse createChallenge(String creatorUsername, ChallengeRequest request) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Convertir el número de dificultad al enum
        int difficultyLevel = Integer.parseInt(request.getDifficulty());
        ChallengeDifficulty difficulty = ChallengeDifficulty.fromLevel(difficultyLevel);

        // Crear el challenge
        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .difficulty(difficulty) // Asignar la dificultad convertida
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        // Agregar los ítems al challenge
        IntStream.range(0, request.getItems().size()).forEach(i -> {
            ChallengeItem item = ChallengeItem.builder()
                    .description(request.getItems().get(i))
                    .orderPosition(i + 1) // Índice basado en 1
                    .challenge(challenge)
                    .build();
            challenge.getItems().add(item);
        });

        // Guardar el challenge
        Challenge savedChallenge = challengeRepository.save(challenge);
        return mapper.toDto(savedChallenge);
    }

    @Override
    public void joinChallenge(Long challengeId, String username) {
        if (participationRepository.existsByChallenge_IdAndUser_Username(challengeId, username)) {
            return; // Ya unido
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge no encontrado"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Crear una nueva participación con ítems completados vacíos
        ChallengeParticipation participation = ChallengeParticipation.builder()
                .challenge(challenge)
                .user(user)
                .completedItems(new HashSet<>()) // Inicializar vacío
                .joinedAt(LocalDateTime.now())
                .build();

        participationRepository.save(participation);
    }

    @Override
    @Transactional
    public ChallengeProgressDto completeItem(Long challengeId,
                                             Long itemId,
                                             String username) {

        // 1) Cargar la participación del usuario en el challenge
        ChallengeParticipation participation =
                participationRepository.findByChallenge_IdAndUser_Username(challengeId, username);

        if (participation == null) {
            throw new IllegalStateException("User has not joined this challenge");
        }

        // 2) Localizar el ítem dentro del challenge
        ChallengeItem item = participation.getChallenge()
                .getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found in this challenge"));

        // 3) Añadirlo al set de completados
        participation.getCompletedItems().add(item);

        // 4) Actualizar timestamp de completado
        if (participation.getCompletedItems().size() ==
                participation.getChallenge().getItems().size()) {
            participation.setCompletedAt(LocalDateTime.now());
        } else {
            participation.setCompletedAt(null);
        }

        // 5) Persistir cambios
        participationRepository.save(participation);

        // 6) → Construir DTO con el *mapper*
        return mapper.toProgressDto(participation);
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

    @Override
    public List<ChallengeResponse> findChallengesByTitle(String title) {
        List<Challenge> challenges = challengeRepository.findByTitleContainingIgnoreCase(title);
        return challenges.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void leaveChallenge(Long challengeId, Long userId) {
        ChallengeParticipation participation = participationRepository
                .findByChallenge_IdAndUser_Id(challengeId, userId);
        if (participation != null) {
            participationRepository.delete(participation);
        } else {
            throw new EntityNotFoundException("Participation not found");
        }
    }

    @Override
    public String getChallengeDifficulty(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found"));

        // Aquí puedes implementar la lógica para determinar la dificultad del challenge
        // Por ejemplo, podrías basarte en el número de ítems o en algún otro criterio
        //la dificultad la determina el creador del challenge
        return challenge.getDifficulty() != null ? challenge.getDifficulty().name() : "UNKNOWN";
    }

    /** Des-marca (un-complete) un item */
    @Override
    @Transactional
    public ChallengeProgressDto uncompleteItem(Long challengeId,
                                               Long itemId,
                                               String username) {

        ChallengeParticipation participation =
                participationRepository.findByChallenge_IdAndUser_Username(challengeId, username);

        if (participation == null) {
            throw new IllegalStateException("User has not joined this challenge");
        }

        boolean removed = participation.getCompletedItems()
                .removeIf(ci -> ci.getId().equals(itemId));

        if (!removed) {
            throw new IllegalStateException("El ítem no estaba completado previamente");
        }

        // Si ahora el usuario ya no tiene todos los ítems completos, borrar el timestamp
        if (participation.getCompletedItems().size() <
                participation.getChallenge().getItems().size()) {
            participation.setCompletedAt(null);
        }

        participationRepository.save(participation);

        // → DTO desde el *mapper*
        return mapper.toProgressDto(participation);
    }
}