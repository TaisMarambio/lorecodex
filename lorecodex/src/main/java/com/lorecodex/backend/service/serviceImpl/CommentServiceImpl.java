package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.CommentRequest;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.notification.event.*;
import com.lorecodex.backend.mapper.CommentMapper;
import com.lorecodex.backend.model.*;
import com.lorecodex.backend.repository.*;
import com.lorecodex.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserListRepository userListRepository;
    private final ChallengeRepository challengeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public void commentOnGuide(Long guideId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        Comment comment = createComment(request, user);
        comment.setGuide(guide);

        commentRepository.save(comment);

        // Notificar al dueño de la guía (si no es el mismo que comenta)
        if (!guide.getUser().getId().equals(user.getId())) {
            eventPublisher.publishEvent(
                    new GuideCommentedEvent(guide.getUser().getId(), user.getUsername(), guide.getTitle())
            );
        }

        // Si es una respuesta, notificar al dueño del comentario padre
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));

            if (!parent.getUser().getId().equals(user.getId())) {
                eventPublisher.publishEvent(
                        new CommentRepliedEvent(
                                parent.getUser().getId(),
                                user.getUsername(),
                                guide.getTitle(),
                                "guide"
                        )
                );
            }
        }
    }

    @Override
    @Transactional
    public void commentOnNews(Long newsId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada"));

        Comment comment = createComment(request, user);
        comment.setNews(news);

        commentRepository.save(comment);

        // SOLO notificar si es una respuesta a un comentario (no al autor de la noticia)
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));

            if (!parent.getUser().getId().equals(user.getId())) {
                eventPublisher.publishEvent(
                        new CommentRepliedEvent(
                                parent.getUser().getId(),
                                user.getUsername(),
                                news.getTitle(),
                                "news"
                        )
                );
            }
        }
    }

    @Override
    @Transactional
    public void commentOnList(Long listId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

        Comment comment = createComment(request, user);
        comment.setUserList(userList);

        commentRepository.save(comment);

        // Notificar al dueño de la lista (si no es el mismo que comenta)
        if (!userList.getUser().getId().equals(user.getId())) {
            eventPublisher.publishEvent(
                    new ListCommentedEvent(userList.getUser().getId(), user.getUsername(), userList.getTitle())
            );
        }

        // Si es una respuesta, notificar al dueño del comentario padre
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));

            if (!parent.getUser().getId().equals(user.getId())) {
                eventPublisher.publishEvent(
                        new CommentRepliedEvent(
                                parent.getUser().getId(),
                                user.getUsername(),
                                userList.getTitle(),
                                "list"
                        )
                );
            }
        }
    }

    @Override
    @Transactional
    public void commentOnChallenge(Long challengeId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge no encontrado"));

        Comment comment = createComment(request, user);
        comment.setChallenge(challenge);

        commentRepository.save(comment);

        // Notificar al creador del challenge (si no es el mismo que comenta)
        if (!challenge.getCreator().getId().equals(user.getId())) {
            eventPublisher.publishEvent(
                    new ChallengeCommentedEvent(
                            challenge.getCreator().getId(),
                            user.getUsername(),
                            challenge.getTitle()
                    )
            );
        }

        // Si es una respuesta, notificar al dueño del comentario padre
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));

            if (!parent.getUser().getId().equals(user.getId())) {
                eventPublisher.publishEvent(
                        new CommentRepliedEvent(
                                parent.getUser().getId(),
                                user.getUsername(),
                                challenge.getTitle(),
                                "challenge"
                        )
                );
            }
        }
    }

    @Override
    public List<CommentResponse> getCommentsForGuide(Long guideId) {
        return commentRepository.findByGuideIdOrderByCreatedAtDesc(guideId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsForGuidePaginated(Long guideId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findByGuideIdAndParentIsNullOrderByCreatedAtDesc(guideId, pageable);
        return commentsPage.map(commentMapper::toResponse);
    }

    @Override
    public List<CommentResponse> getCommentsForNews(Long newsId) {
        return commentRepository.findByNewsIdOrderByCreatedAtDesc(newsId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsForNewsPaginated(Long newsId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findByNewsIdAndParentIsNullOrderByCreatedAtDesc(newsId, pageable);
        return commentsPage.map(commentMapper::toResponse);
    }

    @Override
    public List<CommentResponse> getCommentsForList(Long listId) {
        return commentRepository.findByUserListIdOrderByCreatedAtDesc(listId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsForListPaginated(Long listId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findByUserListIdAndParentIsNullOrderByCreatedAtDesc(listId, pageable);
        return commentsPage.map(commentMapper::toResponse);
    }

    @Override
    public List<CommentResponse> getCommentsForChallenge(Long challengeId) {
        return commentRepository.findByChallengeIdOrderByCreatedAtDesc(challengeId).stream()
                .filter(c -> c.getParent() == null)
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsForChallengePaginated(Long challengeId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findByChallengeIdAndParentIsNullOrderByCreatedAtDesc(challengeId, pageable);
        return commentsPage.map(commentMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = comment.getUser().getId().equals(user.getId());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("No tienes permisos para eliminar este comentario");
        }

        commentRepository.delete(comment);
    }

    private Comment createComment(CommentRequest request, User user) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentario padre no encontrado"));
            comment.setParent(parent);
        }

        return comment;
    }
}