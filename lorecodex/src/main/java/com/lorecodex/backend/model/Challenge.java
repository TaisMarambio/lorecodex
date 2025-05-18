package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeParticipation> participants = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDifficultyRating> difficultyRatings = new ArrayList<>();

    // Campos calculados que se actualizan cuando se modifican las colecciones relacionadas
    @Column(name = "participant_count")
    private Integer participantCount = 0;

    @Column(name = "completion_count")
    private Integer completionCount = 0;

    @Column(name = "average_difficulty", precision = 2, scale = 1)
    private Double averageDifficulty = 0.0;

    // Método para actualizar contador de participantes
    public void updateParticipantCount() {
        this.participantCount = participants.size();
    }

    // Método para actualizar contador de completados
    public void updateCompletionCount() {
        this.completionCount = (int) participants.stream()
                .filter(ChallengeParticipation::isCompleted)
                .count();
    }

    // Método para actualizar dificultad promedio
    public void updateAverageDifficulty() {
        if (difficultyRatings.isEmpty()) {
            this.averageDifficulty = 0.0;
            return;
        }

        double sum = difficultyRatings.stream()
                .mapToInt(ChallengeDifficultyRating::getDifficultyLevel)
                .sum();
        this.averageDifficulty = sum / difficultyRatings.size();
    }
}