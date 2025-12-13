package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Métodos existentes (sin paginación)
    List<Comment> findByGuideIdOrderByCreatedAtDesc(Long guideId);
    List<Comment> findByNewsIdOrderByCreatedAtDesc(Long newsId);
    List<Comment> findByUserListIdOrderByCreatedAtDesc(Long userListId);
    List<Comment> findByChallengeIdOrderByCreatedAtDesc(Long challengeId);

    // Nuevos métodos con paginación (solo comentarios raíz)
    Page<Comment> findByGuideIdAndParentIsNullOrderByCreatedAtDesc(Long guideId, Pageable pageable);
    Page<Comment> findByNewsIdAndParentIsNullOrderByCreatedAtDesc(Long newsId, Pageable pageable);
    Page<Comment> findByUserListIdAndParentIsNullOrderByCreatedAtDesc(Long userListId, Pageable pageable);
    Page<Comment> findByChallengeIdAndParentIsNullOrderByCreatedAtDesc(Long challengeId, Pageable pageable);
}
