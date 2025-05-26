package com.lorecodex.backend.dto.request;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {
    private String title;
    private String content;
    private String coverImageUrl;
    private boolean published;
    private boolean draft;
    private Set<String> tags;
    private List<NewsImageRequest> images;
}