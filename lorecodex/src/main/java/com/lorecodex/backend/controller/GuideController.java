package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GuideResponse> createGuide(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            @RequestParam("isPublished") boolean isPublished,
            @RequestParam("isDraft") boolean isDraft,
            @RequestParam(value = "tags", required = false) Set<String> tags,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        GuideRequest guideRequest = GuideRequest.builder()
                .title(title)
                .content(content)
                .coverImageUrl(coverImageUrl)
                .isPublished(isPublished)
                .isDraft(isDraft)
                .tags(tags)
                .build();

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

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponse> updateGuide(
            @PathVariable Long id,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("isDraft") String isDraftStr,
            @RequestPart("isPublished") String isPublishedStr,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        GuideRequest request = new GuideRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setDraft(Boolean.parseBoolean(isDraftStr));
        request.setPublished(Boolean.parseBoolean(isPublishedStr));

        // para q despúes manejemos coverImage, se maneja acá.

        GuideResponse updated = guideService.updateGuide(id, request);
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
}
