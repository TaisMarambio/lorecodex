package com.lorecodex.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double rating;
    private String genre;
    private String description;

    @OneToMany(mappedBy = "game")
    private List<Guide> guides;
}