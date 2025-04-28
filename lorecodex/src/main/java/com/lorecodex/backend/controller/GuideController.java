package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/create")
    public ResponseEntity<GuideResponse> createGuide(@RequestBody GuideRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        GuideResponse response = guideService.createGuide(request, username);
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

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponse> updateGuide(@PathVariable Long id, @RequestBody GuideRequest request) {
        return ResponseEntity.ok(guideService.updateGuide(id, request));
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
