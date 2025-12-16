package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.mapper.GuideMapper;
import com.lorecodex.backend.model.*;
import com.lorecodex.backend.notification.event.GuideCreatedEvent;
import com.lorecodex.backend.notification.event.GuideUpdatedEvent;
import com.lorecodex.backend.repository.*;
import com.lorecodex.backend.service.GuideService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GuideMapper guideMapper;
    private final ApplicationEventPublisher eventPublisher;

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

    private void assertOwnerOrAdmin(Guide guide) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = isAdmin(auth);
        String principal = auth != null ? auth.getName() : null;
        if (!admin) {
            if (guide.getUser() == null || guide.getUser().getUsername() == null || !guide.getUser().getUsername().equals(principal)) {
                throw new RuntimeException("No autorizado: sólo el creador o admin");
            }
        }
    }

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
        guide.setPublished(false);
        guide.setDraft(true);
        guide.setUser(user);
        guide.setCreatedAt(LocalDateTime.now());
        guide.setUpdatedAt(LocalDateTime.now());

        if (request.getGameId() != null) {
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
            guide.setGame(game);
        }

        if (request.getImages() != null) {
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
        return guideMapper.mapToResponse(saved);
    }

    @Override
    public GuideResponse getGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        return guideMapper.mapToResponse(guide);
    }

    @Override
    public List<GuideResponse> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(guideMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GuideResponse> getAllGuidesPaginated(Pageable pageable) {
        Page<Guide> guidesPage = guideRepository.findAll(pageable);
        return guidesPage.map(guideMapper::mapToResponse);
    }

    @Override
    @Transactional
    public GuideResponse updateGuide(Long id, GuideRequest request) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        assertOwnerOrAdmin(guide);

        guide.setTitle(request.getTitle());
        guide.setContent(request.getContent());
        guide.setCoverImageUrl(request.getCoverImageUrl());
        guide.setTags(request.getTags());
        guide.setUpdatedAt(LocalDateTime.now());

        if (request.getGameId() != null) {
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new RuntimeException("Juego no encontrado"));
            guide.setGame(game);
        }

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

        Guide saved = guideRepository.save(guide);
        if (saved.isPublished()) {
            eventPublisher.publishEvent(new GuideUpdatedEvent(saved.getUser().getId(), saved.getUser().getUsername(), saved.getTitle()));
        }
        return guideMapper.mapToResponse(saved);
    }

    @Override
    public void deleteGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        assertOwnerOrAdmin(guide);
        guideRepository.delete(guide);
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

    @Override
    public List<GuideResponse> getDraftsByUserId(Long userId) {
        List<Guide> drafts = guideRepository.findByUserIdAndIsDraftTrue(userId);
        return drafts.stream()
                .map(guideMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GuideResponse> getDraftsByUserIdPaginated(Long userId, Pageable pageable) {
        Page<Guide> drafts = guideRepository.findByUserIdAndIsDraftTrue(userId, pageable);
        return drafts.map(guideMapper::mapToResponse);
    }

    @Override
    public List<GuideResponse> getPublishedGuidesByUserId(Long id) {
        return List.of();
    }

    @Override
    public List<GuideResponse> getDraftsByUserIdAndPublished(Long id, boolean published) {
        return List.of();
    }

    @Override
    public List<GuideResponse> getPublishedGuidesByUserIdAndDraft(Long id, boolean draft) {
        return List.of();
    }

    @Override
    public List<GuideResponse> getDraftsByUserIdAndPublishedAndDraft(Long id, boolean published, boolean draft) {
        return List.of();
    }

    @Override
    public List<GuideResponse> getPublishedGuides() {
        return guideRepository.findByIsPublishedTrue().stream()
                .map(guideMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GuideResponse> getPublishedGuidesPaginated(Pageable pageable) {
        Page<Guide> guides = guideRepository.findByIsPublishedTrue(pageable);
        return guides.map(guideMapper::mapToResponse);
    }

    @Override
    public String uploadCoverImage(Long guideId, MultipartFile file) {
        return "";
    }

    @Override
    public Optional<GuideResponse> publishGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        assertOwnerOrAdmin(guide);

        if (guide.isPublished()) {
            throw new RuntimeException("La guía ya está publicada");
        }

        guide.setPublished(true);
        guide.setDraft(false);
        guide.setUpdatedAt(LocalDateTime.now());

        Guide savedGuide = guideRepository.save(guide);
        eventPublisher.publishEvent(new GuideCreatedEvent(
                savedGuide.getUser().getId(),
                savedGuide.getUser().getUsername(),
                savedGuide.getTitle()
        ));
        return Optional.of(guideMapper.mapToResponse(savedGuide));
    }

    @Override
    public Optional<GuideResponse> unpublishGuide(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        assertOwnerOrAdmin(guide);

        if (!guide.isPublished()) {
            throw new RuntimeException("La guía ya está sin publicar");
        }

        guide.setPublished(false);
        guide.setDraft(true);
        guide.setUpdatedAt(LocalDateTime.now());

        Guide savedGuide = guideRepository.save(guide);
        return Optional.of(guideMapper.mapToResponse(savedGuide));
    }

    @Override
    public String getAuthorNameByGuideId(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
        return guide.getUser().getUsername();
    }

    @Override
    public List<GuideResponse> getPublishedGuidesByTitle(String title) {
        List<Guide> guides = guideRepository.findByTitleContainingIgnoreCaseAndIsPublishedTrue(title);
        return guides.stream()
                .map(guideMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GuideResponse> getPublishedGuidesByTitlePaginated(String title, Pageable pageable) {
        Page<Guide> guides = guideRepository.findByTitleContainingIgnoreCaseAndIsPublishedTrue(title, pageable);
        return guides.map(guideMapper::mapToResponse);
    }
}
