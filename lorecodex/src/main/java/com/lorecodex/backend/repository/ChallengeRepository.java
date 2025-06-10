package com.lorecodex.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lorecodex.backend.model.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    // List<Challenge> findByAttribute(String attribute);
}