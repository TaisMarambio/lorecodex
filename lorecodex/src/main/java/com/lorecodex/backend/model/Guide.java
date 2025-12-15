package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "guides")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")  // CORREGIDO: TEXT sin límite
    private String content;

    @Column(columnDefinition = "TEXT")  // CORREGIDO: URLs largas
    private String coverImageUrl;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @Column(name = "is_draft", nullable = false)
    private boolean isDraft;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tags y/o categorias
    @ElementCollection
    @CollectionTable(name = "guide_tags", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "tag")
    private Set<String> tags;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // likes (usuarios que dieron like a la guía)
    @ManyToMany
    @JoinTable(
            name = "guide_likes",
            joinColumns = @JoinColumn(name = "guide_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // las imagenes extra que quieres meterle al guide
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuideImage> images;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
