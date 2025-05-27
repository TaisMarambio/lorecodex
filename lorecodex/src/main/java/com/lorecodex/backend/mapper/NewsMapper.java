package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.NewsRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.dto.response.NewsImageResponse;
import com.lorecodex.backend.dto.response.NewsResponse;
import com.lorecodex.backend.model.News;
import com.lorecodex.backend.model.NewsImage;
import com.lorecodex.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsMapper {

    public News toEntity(NewsRequest request, User user) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setCoverImageUrl(request.getCoverImageUrl());
        news.setTags(request.getTags());
        news.setPublished(request.isPublished());
        news.setDraft(request.isDraft());
        news.setUser(user);

        if (request.getImages() != null) {
            List<NewsImage> images = request.getImages().stream()
                    .map(imageRequest -> {
                        NewsImage img = new NewsImage();
                        img.setImageUrl(imageRequest.getImageUrl());
                        img.setCaption(imageRequest.getCaption());
                        img.setNews(news); // vínculo bidireccional
                        return img;
                    })
                    .collect(Collectors.toList());
            news.setImages(images);
        }

        return news;
    }

    public NewsResponse toResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .coverImageUrl(news.getCoverImageUrl())
                .isPublished(news.isPublished())
                .isDraft(news.isDraft())
                .tags(news.getTags())
                .userId(news.getUser() != null ? news.getUser().getId() : null)
                .likeCount(news.getLikedBy() != null ? news.getLikedBy().size() : 0)
                .comments(news.getComments() != null
                        ? news.getComments().stream().map(c -> CommentResponse.builder()
                                .id(c.getId())
                                .content(c.getContent())
                                .createdAt(c.getCreatedAt())
                                .userId(c.getUser().getId())
                                .username(c.getUser().getUsername())
                                .build())
                        .collect(Collectors.toList())
                        : List.of())
                .images(news.getImages() != null
                        ? news.getImages().stream()
                        .map(img -> NewsImageResponse.builder()
                                .imageUrl(img.getImageUrl())
                                .caption(img.getCaption())
                                .build())
                        .collect(Collectors.toList())
                        : List.of())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(NewsRequest request, News news) {
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setCoverImageUrl(request.getCoverImageUrl());
        news.setTags(request.getTags());
        news.setPublished(request.isPublished());
        news.setDraft(request.isDraft());

        // Actualizar imágenes: limpiar y volver a asignar
        if (request.getImages() != null) {
            news.getImages().clear();
            List<NewsImage> images = request.getImages().stream()
                    .map(imageRequest -> {
                        NewsImage img = new NewsImage();
                        img.setImageUrl(imageRequest.getImageUrl());
                        img.setCaption(imageRequest.getCaption());
                        img.setNews(news);
                        return img;
                    })
                    .collect(Collectors.toList());
            news.setImages(images);
        }
    }
}
