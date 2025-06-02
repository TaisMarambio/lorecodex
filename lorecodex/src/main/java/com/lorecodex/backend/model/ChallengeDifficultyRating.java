package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenge_difficulty_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "challenge_id"})
})
public class ChallengeDifficultyRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    /**
     * Difficulty level scale:
     * 1: Very Easy
     * 2: Easy
     * 3: Medium
     * 4: Hard
     * 5: Very Hard
     * 6: Extreme
     */
    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel;
}