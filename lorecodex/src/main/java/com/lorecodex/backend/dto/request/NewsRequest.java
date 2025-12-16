package com.lorecodex.backend.dto.request;

import lombok.*;

import java.util.List;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {
    @NotBlank
    @Size(max = 512)
    private String title;
    @NotBlank
    private String content;
    private String coverImageUrl;
    private boolean published;
    private boolean draft;
    private Set<String> tags;
    private List<NewsImageRequest> images;
}