package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    // Updated create endpoint to accept JSON
    @PostMapping("/create")
    public ResponseEntity<GuideResponse> createGuide(@RequestBody GuideRequest guideRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GuideResponse response = guideService.createGuide(guideRequest, username, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponse> getGuide(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuide(id));
    }

    @GetMapping
    public ResponseEntity<List<GuideResponse>> getAllGuides() {
        return ResponseEntity.ok(guideService.getAllGuides());
    }

    @GetMapping("/published")
    public ResponseEntity<List<GuideResponse>> getPublishedGuides() {
        return ResponseEntity.ok(guideService.getPublishedGuides());
    }

    // Updated update endpoint to accept JSON
    @PutMapping("/{id}")
    public ResponseEntity<GuideResponse> updateGuide(
            @PathVariable Long id,
            @RequestBody GuideRequest guideRequest) {

        GuideResponse updated = guideService.updateGuide(id, guideRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deleteGuide/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeGuide(@PathVariable Long id, @RequestParam Long userId) {
        guideService.likeGuide(id, userId);
        return ResponseEntity.ok().build();
    }

    // Optional: Keep the multipart endpoint for file uploads if needed later
    @PostMapping("/create-with-files")
    public ResponseEntity<GuideResponse> createGuideWithFiles(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            @RequestParam("isPublished") boolean isPublished,
            @RequestParam("isDraft") boolean isDraft,
            @RequestParam(value = "gameId", required = false) Long gameId) {

        GuideRequest guideRequest = GuideRequest.builder()
                .title(title)
                .content(content)
                .coverImageUrl(coverImageUrl)
                .isPublished(isPublished)
                .isDraft(isDraft)
                .gameId(gameId)
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GuideResponse response = guideService.createGuide(guideRequest, username, null);
        return ResponseEntity.ok(response);
    }
}