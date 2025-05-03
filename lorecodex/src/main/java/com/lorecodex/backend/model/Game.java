package com.lorecodex.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private LocalDate releaseDate;
    private Double rating;
    private Integer likes;

    @ElementCollection
    @CollectionTable(name = "game_genres", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "genre")
    private Set<String> genres;

    @ElementCollection
    @CollectionTable(name = "game_devs_and_publishers", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "developer_and_publisher")
    private Set<String> developersAndPublishers;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guide> guides = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRating> userRatings = new ArrayList<>();

    @Column(name = "igdb_id", unique = true)
    private Long igdbId;
}