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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public GuideResponse createGuide(GuideRequest request, String username, List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Guide guide = new Guide();
        guide.setTitle(request.getTitle());
        guide.setContent(request.getContent());
        guide.setCoverImageUrl(request.getCoverImageUrl());
        guide.setTags(request.getTags());
        guide.setPublished(request.isPublished());
        guide.setDraft(request.isDraft());
        guide.setUser(user);
        guide.setCreatedAt(LocalDateTime.now());
        guide.setUpdatedAt(LocalDateTime.now());

        // Handle gameId if present
        if (request.getGameId() != null) {
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + request.getGameId()));
            guide.setGame(game);
        }

        // Handle images if present
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<GuideImage> imagesFromRequest = request.getImages().stream().map(imgReq -> {
                GuideImage image = new GuideImage();
                image.setImageUrl(imgReq.getImageUrl());
                image.setCaption(imgReq.getCaption());
                image.setGuide(guide);
                return image;
            }).collect(Collectors.toList());
            guide.setImages(imagesFromRequest);
        }

        Guide saved = guideRepository.save(guide);
        return mapToResponse(saved);
    }

    @Override
    public GuideResponse getGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide not found with id: " + id));
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
                .orElseThrow(() -> new RuntimeException("Guide not found with id: " + id));

        guide.setTitle(request.getTitle());
        guide.setContent(request.getContent());
        guide.setCoverImageUrl(request.getCoverImageUrl());
        guide.setTags(request.getTags());
        guide.setPublished(request.isPublished());
        guide.setDraft(request.isDraft());
        guide.setUpdatedAt(LocalDateTime.now());

        // Handle gameId update if present
        if (request.getGameId() != null) {
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + request.getGameId()));
            guide.setGame(game);
        }

        // Handle images update
        if (guide.getImages() != null) {
            guide.getImages().clear();
        }
        if (request.getImages() != null && !request.getImages().isEmpty()) {
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
        if (!guideRepository.existsById(id)) {
            throw new RuntimeException("Guide not found with id: " + id);
        }
        guideRepository.deleteById(id);
    }

    @Override
    public void likeGuide(Long guideId, Long userId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guide not found with id: " + guideId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (guide.getLikedBy().contains(user)) {
            guide.getLikedBy().remove(user); // Unlike if already liked
        } else {
            guide.getLikedBy().add(user); // Like if not already liked
        }
        guideRepository.save(guide);
    }

    @Override
    public List<GuideResponse> getDraftsByUserId(Long userId) {
        List<Guide> drafts = guideRepository.findByUserIdAndIsDraftTrue(userId);
        return drafts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<GuideResponse> getPublishedGuidesByUserId(Long id) {
        // You can implement this method if needed
        return List.of();
    }

    @Override
    public List<GuideResponse> getDraftsByUserIdAndPublished(Long id, boolean published) {
        // You can implement this method if needed
        return List.of();
    }

    @Override
    public List<GuideResponse> getPublishedGuidesByUserIdAndDraft(Long id, boolean draft) {
        // You can implement this method if needed
        return List.of();
    }

    @Override
    public List<GuideResponse> getDraftsByUserIdAndPublishedAndDraft(Long id, boolean published, boolean draft) {
        // You can implement this method if needed
        return List.of();
    }

    @Override
    public List<GuideResponse> getPublishedGuides() {
        return guideRepository.findByIsPublishedTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Internal mapper
    private GuideResponse mapToResponse(Guide guide) {
        GuideResponse response = new GuideResponse();
        response.setId(guide.getId());
        response.setTitle(guide.getTitle());
        response.setContent(guide.getContent());
        response.setCoverImageUrl(guide.getCoverImageUrl());
        response.setTags(guide.getTags());
        response.setPublished(guide.isPublished());
        response.setDraft(guide.isDraft());
        response.setUserId(guide.getUser() != null ? guide.getUser().getId() : null);
        response.setLikeCount(guide.getLikedBy() != null ? guide.getLikedBy().size() : 0);
        response.setCreatedAt(guide.getCreatedAt());
        response.setUpdatedAt(guide.getUpdatedAt());

        // Map images if present
        if (guide.getImages() != null && !guide.getImages().isEmpty()) {
            List<GuideImageResponse> images = guide.getImages().stream().map(img -> {
                GuideImageResponse res = new GuideImageResponse();
                res.setImageUrl(img.getImageUrl());
                res.setCaption(img.getCaption());
                return res;
            }).collect(Collectors.toList());
            response.setImages(images);
        }

        // Map comments if present
        if (guide.getComments() != null && !guide.getComments().isEmpty()) {
            List<CommentResponse> comments = guide.getComments().stream().map(comment -> {
                CommentResponse res = new CommentResponse();
                res.setId(comment.getId());
                res.setText(comment.getContent());
                res.setUserId(comment.getUser() != null ? comment.getUser().getId() : null);
                res.setUsername(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
                res.setCreatedAt(comment.getCreatedAt());
                return res;
            }).collect(Collectors.toList());
            response.setComments(comments);
        }

        return response;
    }
}