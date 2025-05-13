package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDifficultyRating> difficultyRatings = new ArrayList<>();

    @Column(name = "average_difficulty")
    private Double averageDifficulty;

    @Column(name = "participants_count")
    private Integer participantsCount = 0;

    @Column(name = "completed_count")
    private Integer completedCount = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void updateAverageDifficulty() {
        if (difficultyRatings == null || difficultyRatings.isEmpty()) {
            this.averageDifficulty = 0.0;
            return;
        }

        double sum = 0.0;
        for (ChallengeDifficultyRating rating : difficultyRatings) {
            sum += rating.getDifficultyLevel();
        }

        this.averageDifficulty = sum / difficultyRatings.size();
    }
}