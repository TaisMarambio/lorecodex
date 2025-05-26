package com.lorecodex.backend.dto.request;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsImageRequest {
    private String imageUrl;
    private String caption;
}
