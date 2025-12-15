package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.GuideService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<GuideResponse> createGuide(
            @RequestBody GuideRequest request,
            @AuthenticationPrincipal User user
    ) {
        GuideResponse response = guideService.createGuide(request, user.getUsername(), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{guideId}/upload-cover")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> uploadCover(
            @PathVariable Long guideId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = guideService.uploadCoverImage(guideId, file);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponse> getGuide(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuide(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<GuideResponse>> getAllGuides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideResponse> guidesPage = guideService.getAllGuidesPaginated(pageable);
        return ResponseEntity.ok(guidesPage.getContent());
    }

    @GetMapping("/all/published")
    public ResponseEntity<List<GuideResponse>> getPublishedGuides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideResponse> guidesPage = guideService.getPublishedGuidesPaginated(pageable);
        return ResponseEntity.ok(guidesPage.getContent());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<GuideResponse> updateGuide(
            @PathVariable Long id,
            @RequestBody GuideRequest request,
            @AuthenticationPrincipal User user
    ) {
        GuideResponse updated = guideService.updateGuide(id, request);
        return ResponseEntity.ok(updated);
    }

    // CORREGIDO: Ahora los admins pueden eliminar cualquier guía
    @DeleteMapping("/deleteGuide/{id}")
    public ResponseEntity<Void> deleteGuide(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        // Validar que sea el autor o admin
        GuideResponse guide = guideService.getGuide(id);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isAuthor = guide.getUserId().equals(user.getId());

        if (!isAdmin && !isAuthor) {
            return ResponseEntity.status(403).build();
        }

        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> likeGuide(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        guideService.likeGuide(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<GuideResponse> publishGuide(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        // Validar que sea el autor o admin
        GuideResponse guide = guideService.getGuide(id);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isAuthor = guide.getUserId().equals(user.getId());

        if (!isAdmin && !isAuthor) {
            return ResponseEntity.status(403).build();
        }

        return guideService.publishGuide(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<GuideResponse> unpublishGuide(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        // Validar que sea el autor o admin
        GuideResponse guide = guideService.getGuide(id);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isAuthor = guide.getUserId().equals(user.getId());

        if (!isAdmin && !isAuthor) {
            return ResponseEntity.status(403).build();
        }

        return guideService.unpublishGuide(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{id}/drafts")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<GuideResponse>> getDraftsByUserId(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Solo el usuario puede ver sus propios drafts (o admin)
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUser.getId().equals(id) && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideResponse> draftsPage = guideService.getDraftsByUserIdPaginated(id, pageable);
        return ResponseEntity.ok(draftsPage.getContent());
    }

    @GetMapping("/{id}/author")
    public ResponseEntity<String> getGuideAuthor(@PathVariable Long id) {
        String author = guideService.getAuthorNameByGuideId(id);
        if (author != null) {
            return ResponseEntity.ok(author);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuideResponse>> searchGuidesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideResponse> guides = guideService.getPublishedGuidesByTitlePaginated(title, pageable);
        return ResponseEntity.ok(guides.getContent());
    }
}
