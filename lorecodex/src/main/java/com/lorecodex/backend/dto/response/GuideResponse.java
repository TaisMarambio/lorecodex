package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideResponse {
    private Long id;
    private String title;
    private String content;
    private String coverImageUrl;
    @Setter
    private boolean isPublished;
    @Setter
    private boolean isDraft;
    private Set<String> tags;
    private Long userId;
    private int likeCount;
    private List<CommentResponse> comments;
    private List<GuideImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
