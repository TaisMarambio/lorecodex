package com.lorecodex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image", columnDefinition = "TEXT")
    private String coverImage;

    // Fecha completa opcional
    private LocalDate releaseDate;
    // Año de lanzamiento (opcional cuando solo se conoce el año)
    @Column(name = "release_year")
    private Integer releaseYear;
    // Indica si la fecha de lanzamiento es desconocida
    @Column(name = "release_date_unknown")
    private Boolean releaseDateUnknown;

    private Double rating;
    private Integer likes;

    @ElementCollection
    @CollectionTable(name = "game_genres", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "genre")
    private Set<String> genres = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "game_devs_and_publishers", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "developer_and_publisher")
    private Set<String> developersAndPublishers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "game_tags", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guide> guides = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRating> userRatings = new ArrayList<>();

    // SOLUCIÓN: Agregar cascade para game_notes
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameNote> gameNotes = new ArrayList<>();

    @Column(name = "igdb_id", unique = true)
    private Long igdbId;

    @PrePersist
    private void setCreationTimestamp() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        // Normalizar valores nulos
        if (this.releaseDateUnknown == null) {
            this.releaseDateUnknown = false;
        }
    }
}
