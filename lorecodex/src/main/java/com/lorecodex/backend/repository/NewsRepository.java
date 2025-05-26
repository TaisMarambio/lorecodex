package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByIsPublishedTrueOrderByCreatedAtDesc();

    List<News> findByTagsContainingIgnoreCase(String tag);

    List<News> findByUserIdOrderByCreatedAtDesc(Long userId);
}