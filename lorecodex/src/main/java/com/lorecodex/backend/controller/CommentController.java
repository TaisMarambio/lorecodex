package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/guide/{guideId}")
    public ResponseEntity<Void> commentOnGuide(
            @PathVariable Long guideId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnGuide(guideId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/news/{newsId}")
    public ResponseEntity<Void> commentOnNews(
            @PathVariable Long newsId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        commentService.commentOnNews(newsId, user.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/guide/{guideId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForGuide(
            @PathVariable Long guideId
    ) {
        return ResponseEntity.ok(commentService.getCommentsForGuide(guideId));
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForNews(
            @PathVariable Long newsId
    ) {
        return ResponseEntity.ok(commentService.getCommentsForNews(newsId));
    }
}
