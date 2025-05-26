package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE :tag MEMBER OF n.tags AND n.isPublished = true ORDER BY n.createdAt DESC")
    List<News> findByTag(@Param("tag") String tag);

    List<News> findByIsPublishedTrueOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.user.id = :userId")
    List<News> findByUserId(@Param("userId") Long userId);

    List<News> findByIsPublishedTrueOrderByCreatedAtDesc();

    List<News> findByTagsContainingIgnoreCase(String tag);

    List<News> findByUserIdOrderByCreatedAtDesc(Long userId);

}