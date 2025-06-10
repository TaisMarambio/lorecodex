package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.GameNoteRequest;
import com.lorecodex.backend.dto.response.GameNoteResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.GameNote;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.GameNoteRepository;
import com.lorecodex.backend.repository.GameRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.GameNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameNoteServiceImpl implements GameNoteService {

    private final GameNoteRepository noteRepo;
    private final GameRepository gameRepo;
    private final UserRepository userRepo;

    @Override
    public void createNote(Long gameId, Long userId, GameNoteRequest request) {
        Game game = gameRepo.findById(gameId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();

        GameNote note = new GameNote();
        note.setGame(game);
        note.setUser(user);
        note.setContent(request.getContent());

        noteRepo.save(note);
    }

    @Override
    public List<GameNoteResponse> getNotes(Long gameId, Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        return noteRepo.findByGameIdAndUserIdOrderByCreatedAtDesc(gameId, user.getId())
                .stream()
                .map(note -> {
                    GameNoteResponse res = new GameNoteResponse();
                    res.setId(note.getId());
                    res.setContent(note.getContent());
                    res.setCreatedAt(note.getCreatedAt());
                    return res;
                })
                .toList();
    }

    @Override
    public void deleteNote(Long gameId, Long noteId, Long userId) {
        GameNote note = noteRepo.findById(noteId)
                .filter(n -> n.getGame().getId().equals(gameId) && n.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found or not authorized"));

        noteRepo.delete(note);
    }

    @Override
    public void updateNote(Long gameId, Long noteId, Long userId, GameNoteRequest request) {
        GameNote note = noteRepo.findById(noteId)
                .filter(n -> n.getGame().getId().equals(gameId) && n.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found or not authorized"));

        note.setContent(request.getContent());
        noteRepo.save(note);
    }
}
