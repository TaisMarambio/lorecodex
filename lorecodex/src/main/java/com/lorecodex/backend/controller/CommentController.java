package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ============== GUIDE COMMENTS ==============

    @PostMapping("/guide/{guideId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> commentOnGuide(
            @PathVariable Long guideId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnGuide(guideId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/guide/{guideId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForGuide(
            @PathVariable Long guideId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        // Si se especifican parámetros de paginación
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponse> commentsPage = commentService.getCommentsForGuidePaginated(guideId, pageable);
            return ResponseEntity.ok(commentsPage.getContent());
        }
        // Si no, retornar todos
        return ResponseEntity.ok(commentService.getCommentsForGuide(guideId));
    }

    // ============== NEWS COMMENTS ==============

    @PostMapping("/news/{newsId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> commentOnNews(
            @PathVariable Long newsId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnNews(newsId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForNews(
            @PathVariable Long newsId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponse> commentsPage = commentService.getCommentsForNewsPaginated(newsId, pageable);
            return ResponseEntity.ok(commentsPage.getContent());
        }
        return ResponseEntity.ok(commentService.getCommentsForNews(newsId));
    }

    // ============== LIST COMMENTS ==============

    @PostMapping("/list/{listId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> commentOnList(
            @PathVariable Long listId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnList(listId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForList(
            @PathVariable Long listId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponse> commentsPage = commentService.getCommentsForListPaginated(listId, pageable);
            return ResponseEntity.ok(commentsPage.getContent());
        }
        return ResponseEntity.ok(commentService.getCommentsForList(listId));
    }

    // ============== CHALLENGE COMMENTS ==============

    @PostMapping("/challenge/{challengeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> commentOnChallenge(
            @PathVariable Long challengeId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnChallenge(challengeId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForChallenge(
            @PathVariable Long challengeId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponse> commentsPage = commentService.getCommentsForChallengePaginated(challengeId, pageable);
            return ResponseEntity.ok(commentsPage.getContent());
        }
        return ResponseEntity.ok(commentService.getCommentsForChallenge(challengeId));
    }

    // ============== DELETE COMMENT (Owner or Admin) ==============

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(commentId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
