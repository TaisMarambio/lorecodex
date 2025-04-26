package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.dto.response.GuideImageResponse;
import com.lorecodex.backend.dto.response.CommentResponse;
import com.lorecodex.backend.model.*;
import com.lorecodex.backend.repository.*;
import com.lorecodex.backend.service.GuideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public GuideResponse createGuide(GuideRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));

        Guide guide = new Guide();
        guide.setTitle(request.getTitle());
        guide.setContent(request.getContent());
        guide.setCoverImageUrl(request.getCoverImageUrl());
        guide.setTags(request.getTags());
        guide.setPublished(request.isPublished());
        guide.setDraft(request.isDraft());
        guide.setUser(user);
        guide.setGame(game);
        guide.setCreatedAt(LocalDateTime.now());
        guide.setUpdatedAt(LocalDateTime.now());

        if (request.getImages() != null) {
            List<GuideImage> images = request.getImages().stream().map(imgReq -> {
                GuideImage image = new GuideImage();
                image.setImageUrl(imgReq.getImageUrl());
                image.setCaption(imgReq.getCaption());
                image.setGuide(guide);
                return image;
            }).collect(Collectors.toList());
            guide.setImages(images);
        }

        Guide saved = guideRepository.save(guide);
        return mapToResponse(saved);
    }

    @Override
    public GuideResponse getGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        return mapToResponse(guide);
    }

    @Override
    public List<GuideResponse> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GuideResponse updateGuide(Long id, GuideRequest request) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        guide.setTitle(request.getTitle());
        guide.setContent(request.getContent());
        guide.setCoverImageUrl(request.getCoverImageUrl());
        guide.setTags(request.getTags());
        guide.setPublished(request.isPublished());
        guide.setDraft(request.isDraft());
        guide.setUpdatedAt(LocalDateTime.now());

        guide.getImages().clear();
        if (request.getImages() != null) {
            List<GuideImage> newImages = request.getImages().stream().map(imgReq -> {
                GuideImage image = new GuideImage();
                image.setImageUrl(imgReq.getImageUrl());
                image.setCaption(imgReq.getCaption());
                image.setGuide(guide);
                return image;
            }).toList();
            guide.getImages().addAll(newImages);
        }

        return mapToResponse(guideRepository.save(guide));
    }

    @Override
    public void deleteGuide(Long id) {
        guideRepository.deleteById(id);
    }

    @Override
    public void likeGuide(Long guideId, Long userId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        guide.getLikedBy().add(user); // podriamos controlar si ya le dieron like o no
        guideRepository.save(guide);
    }

    //Mapper interno
    private GuideResponse mapToResponse(Guide guide) {
        GuideResponse response = new GuideResponse();
        response.setId(guide.getId());
        response.setTitle(guide.getTitle());
        response.setContent(guide.getContent());
        response.setCoverImageUrl(guide.getCoverImageUrl());
        response.setIsPublished(guide.isPublished());
        response.setIsDraft(guide.isDraft());
        response.setTags(guide.getTags());
        response.setUserId(guide.getUser().getId());
        response.setGameId(guide.getGame().getId());
        response.setLikeCount(guide.getLikedBy() != null ? guide.getLikedBy().size() : 0);
        response.setCreatedAt(guide.getCreatedAt());
        response.setUpdatedAt(guide.getUpdatedAt());

        if (guide.getImages() != null) {
            List<GuideImageResponse> images = guide.getImages().stream().map(img -> {
                GuideImageResponse res = new GuideImageResponse();
                res.setImageUrl(img.getImageUrl());
                res.setCaption(img.getCaption());
                return res;
            }).collect(Collectors.toList());
            response.setImages(images);
        }

        if (guide.getComments() != null) {
            List<CommentResponse> comments = guide.getComments().stream().map(comment -> {
                CommentResponse res = new CommentResponse();
                res.setId(comment.getId());
                res.setText(comment.getContent());
                res.setUserId(comment.getUser().getId());
                res.setUsername(comment.getUser().getUsername()); // asumiendo que hay un campo username
                res.setCreatedAt(comment.getCreatedAt());
                return res;
            }).collect(Collectors.toList());
            response.setComments(comments);
        }

        return response;
    }
}
