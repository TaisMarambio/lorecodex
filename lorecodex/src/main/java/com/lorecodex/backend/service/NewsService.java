package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.NewsRequest;
import com.lorecodex.backend.dto.response.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NewsService {
    List<NewsResponse> findAllPublishedNews();
    Page<NewsResponse> findAllPublishedNewsPaginated(Pageable pageable);

    Optional<NewsResponse> findById(Long id);

    NewsResponse createNews(NewsRequest request);

    NewsResponse updateNews(Long id, NewsRequest request);

    void delete(Long id);

    List<NewsResponse> findByTag(String tag);
    Page<NewsResponse> findByTagPaginated(String tag, Pageable pageable);

    List<NewsResponse> findRecentNews(int limit);

    Optional<NewsResponse> toggleLike(Long id);

    List<NewsResponse> findNewsByUserId(Long userId);
    Page<NewsResponse> findNewsByUserIdPaginated(Long userId, Pageable pageable);

    Optional<NewsResponse> publishNews(Long id);

    Optional<NewsResponse> unpublishNews(Long id);
}
