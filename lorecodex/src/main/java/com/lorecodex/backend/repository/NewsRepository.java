package com.lorecodex.backend.repository;

import com.lorecodex.backend.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByTitleContaining(String title);
}
