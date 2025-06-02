package com.lorecodex.backend.dto.request;

import lombok.*;

import java.util.Set;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideRequest {
    private String title;
    private String content;
    private String coverImageUrl;
    private boolean isPublished;
    private boolean isDraft;
    private Set<String> tags;
    private Long userId;
    private Long gameId;
    private List<GuideImageRequest> images;
}