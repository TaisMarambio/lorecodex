package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Game> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    Optional<Game> findByTitleIgnoreCase(String title);
    Optional<Game> findByIgdbId(Long igdbId);
    Page<Game> findByTagsContainingIgnoreCase(String tag, Pageable pageable);

    /**
     * Busca juegos ordenados por popularidad (cantidad de ratings)
     * Usa LEFT JOIN para incluir juegos sin ratings (con count = 0)
     */
    @Query("SELECT g FROM Game g " +
            "LEFT JOIN g.userRatings ur " +
            "GROUP BY g.id " +
            "ORDER BY COUNT(ur) DESC")
    Page<Game> findAllOrderByRatingCount(Pageable pageable);

    /**
     * Busca juegos por título ordenados por popularidad
     */
    @Query("SELECT g FROM Game g " +
            "LEFT JOIN g.userRatings ur " +
            "WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "GROUP BY g.id " +
            "ORDER BY COUNT(ur) DESC")
    Page<Game> findByTitleOrderByRatingCount(@Param("title") String title, Pageable pageable);
}
