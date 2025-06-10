package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.request.GameNoteRequest;
import com.lorecodex.backend.dto.response.GameNoteResponse;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.GameNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes/{gameId}")
@RequiredArgsConstructor
public class GameNoteController {

    private final GameNoteService noteService;

    @PostMapping
    public ResponseEntity<Void> createNote(
            @PathVariable Long gameId,
            @RequestBody GameNoteRequest request,
            @AuthenticationPrincipal User user
    ) {
        noteService.createNote(gameId, user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GameNoteResponse>> getNotes(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(noteService.getNotes(gameId, user.getId()));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long gameId,
            @PathVariable Long noteId,
            @AuthenticationPrincipal User user
    ) {
        noteService.deleteNote(gameId, noteId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Void> updateNote(
            @PathVariable Long gameId,
            @PathVariable Long noteId,
            @RequestBody GameNoteRequest request,
            @AuthenticationPrincipal User user
    ) {
        noteService.updateNote(gameId, noteId, user.getId(), request);
        return ResponseEntity.ok().build();
    }

}
