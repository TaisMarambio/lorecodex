package com.lorecodex.backend.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsImageResponse {
    private String imageUrl;
    private String caption;
}
