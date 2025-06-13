package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GuideResponse> createGuide(
            @RequestBody GuideRequest request,
            @AuthenticationPrincipal User user
    ) {
        GuideResponse response = guideService.createGuide(request, user.getUsername(), null);
        return ResponseEntity.ok(response);
    }

    // Subir imagen de portada por separado
    @PostMapping("/{guideId}/upload-cover")
    public ResponseEntity<String> uploadCover(
            @PathVariable Long guideId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = guideService.uploadCoverImage(guideId, file);
        return ResponseEntity.ok(url);
    }

    //Obtener una guia por id
    @GetMapping("/{id}")
    public ResponseEntity<GuideResponse> getGuide(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuide(id));
    }

    // obtener todas las guías
    @GetMapping("/all")
    public ResponseEntity<List<GuideResponse>> getAllGuides() {
        return ResponseEntity.ok(guideService.getAllGuides());
    }

    // obtener solo las guías publicadas
    @GetMapping("/all/published")
    public ResponseEntity<List<GuideResponse>> getPublishedGuides() {
        return ResponseEntity.ok(guideService.getPublishedGuides());
    }

    // actualizar guia
    @PutMapping("/update/{id}")
    public ResponseEntity<GuideResponse> updateGuide(
            @PathVariable Long id,
            @RequestBody GuideRequest request,
            @AuthenticationPrincipal User user
    ) {
        GuideResponse updated = guideService.updateGuide(id, request);
        return ResponseEntity.ok(updated);
    }

    // eliminar
    @DeleteMapping("/deleteGuide/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    // Like con usuario autenticado, no se pasa userId
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeGuide(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        guideService.likeGuide(id, user.getId());
        return ResponseEntity.ok().build();
    }

    //publicar una guia
    @PostMapping("/{id}/publish")
    public ResponseEntity<GuideResponse> publishGuide(@PathVariable Long id) {
        return guideService.publishGuide(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // despublicar una guia
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<GuideResponse> unpublishGuide(@PathVariable Long id) {
        return guideService.unpublishGuide(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //obtener todos los borradores de un usuario
    @GetMapping("/user/{id}/drafts")
    public ResponseEntity<List<GuideResponse>> getDraftsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getDraftsByUserId(id));
    }

    //quiero obtener el autor de una guia específica
    @GetMapping("/{id}/author")
    public ResponseEntity<String> getGuideAuthor(@PathVariable Long id) {
        String author = guideService.getAuthorNameByGuideId(id);
        if (author != null) {
            return ResponseEntity.ok(author);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // buscar guías por título
    @GetMapping("/search")
    public ResponseEntity<List<GuideResponse>> searchGuidesByTitle(@RequestParam String title) {
        List<GuideResponse> guides = guideService.getPublishedGuidesByTitle(title);
        return ResponseEntity.ok(guides);
    }
}
