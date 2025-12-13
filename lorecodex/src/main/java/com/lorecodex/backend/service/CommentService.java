package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    void commentOnGuide(Long guideId, String username, CommentRequest request);

    void commentOnNews(Long newsId, String username, CommentRequest request);
    void commentOnList(Long listId, String username, CommentRequest request);
    void commentOnChallenge(Long challengeId, String username, CommentRequest request);

    List<CommentResponse> getCommentsForGuide(Long guideId);

    List<CommentResponse> getCommentsForNews(Long newsId);
    List<CommentResponse> getCommentsForList(Long listId);
    List<CommentResponse> getCommentsForChallenge(Long challengeId);

    // Nuevos métodos con paginación
    Page<CommentResponse> getCommentsForGuidePaginated(Long guideId, Pageable pageable);
    Page<CommentResponse> getCommentsForNewsPaginated(Long newsId, Pageable pageable);
    Page<CommentResponse> getCommentsForListPaginated(Long listId, Pageable pageable);
    Page<CommentResponse> getCommentsForChallengePaginated(Long challengeId, Pageable pageable);

    void deleteComment(Long commentId, String username);
}
