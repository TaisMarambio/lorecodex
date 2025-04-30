package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
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
    Optional<UserRating> findByUserAndGame(User user, Game game);
    List<UserRating> findByGame(Game game);
    List<UserRating> findByUser(User user);
    void deleteByUserAndGame(User user, Game game);
}