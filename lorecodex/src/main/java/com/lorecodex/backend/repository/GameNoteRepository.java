package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.GameNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameNoteRepository extends JpaRepository<GameNote, Long> {
    List<GameNote> findByGameIdAndUserIdOrderByCreatedAtDesc(Long gameId, Long userId);
}
