package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByIsPublishedTrueOrderByCreatedAtDesc();
    Page<News> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    List<News> findByTagsContainingIgnoreCase(String tag);
    Page<News> findByTagsContainingIgnoreCase(String tag, Pageable pageable);

    List<News> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<News> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
