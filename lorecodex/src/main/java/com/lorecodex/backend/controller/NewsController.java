package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.NewsRequest;
import com.lorecodex.backend.dto.response.NewsResponse;
import com.lorecodex.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsResponse>> getAllPublishedNews() {
        return ResponseEntity.ok(newsService.findAllPublishedNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return newsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponse> createNews(@RequestBody NewsRequest request) {
        return ResponseEntity.status(201).body(newsService.createNews(request));
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
    public ResponseEntity<List<NewsResponse>> getNewsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(newsService.findByTag(tag));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<NewsResponse>> getRecentNews(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(newsService.findRecentNews(limit));
    }

    @PostMapping("/{id}/toggle-like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NewsResponse> toggleLike(@PathVariable Long id) {
        return newsService.toggleLike(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NewsResponse>> getNewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(newsService.findNewsByUserId(userId));
    }
}
