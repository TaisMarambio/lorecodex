package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.dto.response.GuideImageResponse;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.model.Guide;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GuideMapper {
    public GuideResponse mapToResponse(Guide guide) {
        GuideResponse response = new GuideResponse();
        response.setId(guide.getId());
        response.setTitle(guide.getTitle());
        response.setContent(guide.getContent());
        response.setCoverImageUrl(guide.getCoverImageUrl());
        response.setTags(guide.getTags());
        response.setCreatedAt(guide.getCreatedAt());
        response.setUpdatedAt(guide.getUpdatedAt());
        response.setPublished(guide.isPublished());
        response.setAuthorId(guide.getUser().getId());
        response.setAuthorUsername(guide.getUser().getUsername());
        response.setDraft(guide.isDraft());

        if (guide.getImages() != null) {
            List<GuideImageResponse> images = guide.getImages().stream().map(img -> {
                GuideImageResponse res = new GuideImageResponse();
                res.setImageUrl(img.getImageUrl());
                res.setCaption(img.getCaption());
                return res;
            }).collect(Collectors.toList());
            response.setImages(images);
        }

        if (guide.getComments() != null) {
            List<CommentResponse> comments = guide.getComments().stream().map(comment -> {
                CommentResponse res = new CommentResponse();
                res.setId(comment.getId());
                res.setContent(comment.getContent());
                res.setUserId(comment.getUser().getId());
                res.setUsername(comment.getUser().getUsername());
                res.setCreatedAt(comment.getCreatedAt());
                return res;
            }).collect(Collectors.toList());
            response.setComments(comments);
        }

        return response;
    }
}
