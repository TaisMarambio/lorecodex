package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Challenge;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByCreator(User creator);
    List<Challenge> findByGame(Game game);

    @Query("SELECT c FROM Challenge c ORDER BY c.createdAt DESC")
    List<Challenge> findAllOrderByCreatedAtDesc();

    @Query("SELECT c FROM Challenge c WHERE c.game.id = :gameId ORDER BY c.createdAt DESC")
    List<Challenge> findByGameIdOrderByCreatedAtDesc(@Param("gameId") Long gameId);

    @Query("SELECT c FROM Challenge c WHERE c.creator.id = :userId ORDER BY c.createdAt DESC")
    List<Challenge> findByCreatorIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}