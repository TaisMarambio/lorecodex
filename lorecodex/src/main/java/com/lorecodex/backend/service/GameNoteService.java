package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.GameNoteRequest;
import com.lorecodex.backend.dto.response.GameNoteResponse;
import com.lorecodex.backend.model.User;

import java.util.List;

public interface GameNoteService {
    void createNote(Long gameId, Long userId, GameNoteRequest request);
    List<GameNoteResponse> getNotes(Long gameId, Long userId);
    void deleteNote(Long gameId, Long noteId, Long userId);
    void updateNote(Long gameId, Long noteId, Long userId, GameNoteRequest request);

}
