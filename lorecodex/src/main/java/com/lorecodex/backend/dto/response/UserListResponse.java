package com.lorecodex.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserListResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private List<ListItemResponse> items;
}
