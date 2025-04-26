package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    // aca podemos agregar filtros por juego,usuario, etc
    // List<Guide> findByGameId(Integer gameId);
    // List<Guide> findByUserId(Integer userId);
}
