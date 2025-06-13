package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.dto.response.NewsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GuideService {
    GuideResponse createGuide(GuideRequest request, String username, List<MultipartFile> images); // <-- AGREGAMOS images
    GuideResponse getGuide(Long id);
    List<GuideResponse> getAllGuides();
    GuideResponse updateGuide(Long id, GuideRequest request);
    void deleteGuide(Long id);
    void likeGuide(Long guideId, Long userId);
    List<GuideResponse> getDraftsByUserId(Long id);
    List<GuideResponse> getPublishedGuidesByUserId(Long id);
    List<GuideResponse> getDraftsByUserIdAndPublished(Long id, boolean published);
    List<GuideResponse> getPublishedGuidesByUserIdAndDraft(Long id, boolean draft);
    List<GuideResponse> getDraftsByUserIdAndPublishedAndDraft(Long id, boolean published, boolean draft);
    List<GuideResponse> getPublishedGuides();
    String uploadCoverImage(Long guideId, MultipartFile file);
    Optional<GuideResponse> publishGuide(Long id);
    Optional<GuideResponse> unpublishGuide(Long id);
    String getAuthorNameByGuideId(Long guideId);
    List<GuideResponse> getPublishedGuidesByTitle(String title);

}
