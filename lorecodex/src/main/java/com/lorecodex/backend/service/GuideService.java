package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.GuideRequest;
import com.lorecodex.backend.dto.response.GuideResponse;

import java.util.List;

public interface GuideService {
    GuideResponse createGuide(GuideRequest request, String username);
    GuideResponse getGuide(Long id);
    List<GuideResponse> getAllGuides();
    GuideResponse updateGuide(Long id, GuideRequest request);
    void deleteGuide(Long id);
    void likeGuide(Long guideId, Long userId);

}
