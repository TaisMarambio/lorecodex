package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void commentOnGuide(Long guideId, String username, CommentRequest request);

    void commentOnNews(Long newsId, String username, CommentRequest request);

    List<CommentResponse> getCommentsForGuide(Long guideId);

    List<CommentResponse> getCommentsForNews(Long newsId);
}
