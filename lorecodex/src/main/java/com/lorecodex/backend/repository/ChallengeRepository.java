package com.lorecodex.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lorecodex.backend.model.Challenge;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    // List<Challenge> findByAttribute(String attribute);
    List<Challenge> findByTitleContainingIgnoreCase(String title);
}