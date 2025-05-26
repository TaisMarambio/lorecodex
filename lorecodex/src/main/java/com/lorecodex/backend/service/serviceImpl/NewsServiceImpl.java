package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.NewsRequest;
import com.lorecodex.backend.dto.response.NewsResponse;
import com.lorecodex.backend.mapper.NewsMapper;
import com.lorecodex.backend.model.News;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.NewsRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.NewsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    @Override
    public List<NewsResponse> findAllPublishedNews() {
        return newsRepository.findByIsPublishedTrueOrderByCreatedAtDesc().stream()
                .map(newsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NewsResponse> findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toResponse);
    }

    @Override
    public NewsResponse createNews(NewsRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        News news = newsMapper.toEntity(request, user);
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponse(newsRepository.save(news));
    }


    @Override
    public NewsResponse updateNews(Long id, NewsRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));

        newsMapper.updateEntityFromRequest(request, news);
        news.setUpdatedAt(LocalDateTime.now());

        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("Noticia no encontrada");
        }
        newsRepository.deleteById(id);
    }

    @Override
    public List<NewsResponse> findByTag(String tag) {
        return newsRepository.findByTagsContainingIgnoreCase(tag).stream()
                .map(newsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsResponse> findRecentNews(int limit) {
        return newsRepository.findByIsPublishedTrueOrderByCreatedAtDesc().stream()
                .limit(limit)
                .map(newsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NewsResponse> toggleLike(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));

        Long currentUserId = 1L;
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (news.getLikedBy().contains(user)) {
            news.getLikedBy().remove(user);
        } else {
            news.getLikedBy().add(user);
        }

        return Optional.of(newsMapper.toResponse(newsRepository.save(news)));
    }

    @Override
    public List<NewsResponse> findNewsByUserId(Long userId) {
        return newsRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(newsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NewsResponse> publishNews(Long id) {
        return newsRepository.findById(id).map(news -> {
            news.setPublished(true);
            news.setDraft(false);
            news.setUpdatedAt(LocalDateTime.now());
            return newsMapper.toResponse(newsRepository.save(news));
        });
    }

    @Override
    public Optional<NewsResponse> unpublishNews(Long id) {
        return newsRepository.findById(id).map(news -> {
            news.setPublished(false);
            news.setDraft(true);
            news.setUpdatedAt(LocalDateTime.now());
            return newsMapper.toResponse(newsRepository.save(news));
        });
    }
}
