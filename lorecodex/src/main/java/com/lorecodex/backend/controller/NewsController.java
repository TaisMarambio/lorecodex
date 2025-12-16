package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.NewsRequest;
import com.lorecodex.backend.dto.response.NewsResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsResponse>> getAllPublishedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NewsResponse> newsPage = newsService.findAllPublishedNewsPaginated(pageable);
        return ResponseEntity.ok(newsPage.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return newsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> createNews(@RequestBody @Valid NewsRequest request,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(newsService.createNews(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> updateNews(
            @PathVariable Long id,
            @RequestBody NewsRequest request
    ) {
        return ResponseEntity.ok(newsService.updateNews(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> publishNews(@PathVariable Long id) {
        return newsService.publishNews(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> unpublishNews(@PathVariable Long id) {
        return newsService.unpublishNews(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<NewsResponse>> getNewsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NewsResponse> newsPage = newsService.findByTagPaginated(tag, pageable);
        return ResponseEntity.ok(newsPage.getContent());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<NewsResponse>> getRecentNews(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(newsService.findRecentNews(limit));
    }

    @PostMapping("/{id}/toggle-like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NewsResponse> toggleLike(@PathVariable Long id,
                                                   @AuthenticationPrincipal User user) {
        return newsService.toggleLike(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NewsResponse>> getNewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NewsResponse> newsPage = newsService.findNewsByUserIdPaginated(userId, pageable);
        return ResponseEntity.ok(newsPage.getContent());
    }
}
