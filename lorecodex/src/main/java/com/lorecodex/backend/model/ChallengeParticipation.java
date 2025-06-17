package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name="challenge_participation")
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    // Items completados por el usuario
    @ManyToMany
    @JoinTable(name = "challenge_participation_items",
            joinColumns = @JoinColumn(name = "participation_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    @Builder.Default
    private Set<ChallengeItem> completedItems = new HashSet<>();

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
        this.completedAt = null; // Se establece a null al unirse, se actualizará al completar
    }

}