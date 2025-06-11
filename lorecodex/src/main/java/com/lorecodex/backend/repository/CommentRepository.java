package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGuideIdOrderByCreatedAtDesc(Long guideId);
    List<Comment> findByNewsIdOrderByCreatedAtDesc(Long newsId);
}
