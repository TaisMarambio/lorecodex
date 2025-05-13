package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByCreator(User creator);
    List<Challenge> findByGame(Game game);
    Page<Challenge> findAll(Pageable pageable);

    @Query("SELECT c FROM Challenge c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.game.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Challenge> searchChallenges(String searchTerm, Pageable pageable);
}