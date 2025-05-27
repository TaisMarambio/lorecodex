package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.notification.event.GuideCommentedEvent;
import com.lorecodex.backend.notification.event.NewsCommentedEvent;
import com.lorecodex.backend.mapper.CommentMapper;
import com.lorecodex.backend.model.*;
import com.lorecodex.backend.repository.*;
import com.lorecodex.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final GuideRepository guideRepository;
    private final NewsRepository newsRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentMapper commentMapper;

    @Override
    public void commentOnGuide(Long guideId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setGuide(guide);
        comment.setCreatedAt(LocalDateTime.now());

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));
            comment.setParent(parent);
        }

        commentRepository.save(comment);

        eventPublisher.publishEvent(
                new GuideCommentedEvent(guide.getUser().getId(), user.getUsername(), guide.getTitle())
        );
    }

    @Override
    public void commentOnNews(Long newsId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setNews(news);
        comment.setCreatedAt(LocalDateTime.now());

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));
            comment.setParent(parent);
        }

        commentRepository.save(comment);

        eventPublisher.publishEvent(
                new NewsCommentedEvent(news.getUser().getId(), user.getUsername(), news.getTitle())
        );
    }

    @Override
    public List<CommentResponse> getCommentsForGuide(Long guideId) {
        return commentRepository.findByGuideIdOrderByCreatedAtDesc(guideId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsForNews(Long newsId) {
        return commentRepository.findByNewsIdOrderByCreatedAtDesc(newsId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
