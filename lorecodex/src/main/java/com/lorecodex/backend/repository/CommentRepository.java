package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGuideIdOrderByCreatedAtDesc(Long guideId);
    List<Comment> findByNewsIdOrderByCreatedAtDesc(Long newsId);
}
