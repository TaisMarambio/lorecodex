package com.lorecodex.backend.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
public class NewsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String caption;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;
}
