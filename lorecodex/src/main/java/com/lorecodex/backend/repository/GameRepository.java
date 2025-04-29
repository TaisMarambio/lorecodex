package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByTitle(String title);

    @Query("SELECT g FROM Game g WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Game> findByTitleContainingIgnoreCase(@Param("title") String title);

    List<Game> findByGenresContaining(String genre);
    Optional<Game> findByTitle(String name);
    //falta agregar otras busquedas
}
