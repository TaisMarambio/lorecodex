package com.lorecodex.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private String title;
    private String content;

    // ✅ NUEVO: Agregar summary
    private String summary;

    // ✅ CAMBIO: Renombrado para consistencia
    private String coverImage;

    private boolean isPublished;
    private boolean isDraft;
    private Set<String> tags;
    private Long userId;

    // ✅ NUEVO: Agregar authorUsername para mostrar en frontend
    private String authorUsername;

    private int likeCount;
    private List<CommentResponse> comments;
    private List<NewsImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
