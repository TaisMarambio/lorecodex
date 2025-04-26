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

    private String title;
    private String content; // Puede ser texto plano, HTML o Markdown, chequear
    private String coverImageUrl;

    private boolean isPublished;
    private boolean isDraft;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tags y/o categorias (pueden ser strings simples o una entidad aparte, revisar)
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

    // likes (usuarios que dieron like ca la guia)
    @ManyToMany
    @JoinTable(
            name = "guide_likes",
            joinColumns = @JoinColumn(name = "guide_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // las imagenes extra q queres meterle al guide
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuideImage> images;
}
