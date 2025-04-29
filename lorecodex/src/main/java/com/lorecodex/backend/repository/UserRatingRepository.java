package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> findByGameId(Long gameId);
    Optional<UserRating> findByUserIdAndGameId(Long userId, Long gameId);

    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.game.id = :gameId")
    Double calculateAverageRatingForGame(Long gameId);

    @Modifying
    @Query("DELETE FROM UserRating ur WHERE ur.user.id = :userId AND ur.game.id = :gameId")
    void deleteByUserIdAndGameId(@Param("userId") Long userId, @Param("gameId") Long gameId);
}