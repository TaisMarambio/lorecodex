package com.lorecodex.backend.dto.response.challenge;

import com.lorecodex.backend.dto.response.CommentResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private String creatorUsername;
    private Long creatorUserId;
    private List<ChallengeItemDto> items;
    private List<CommentResponse> comments;
}
