package com.lorecodex.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@Getter
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String bio;

    private int followersCount;
    private int followingCount;
    private boolean isFollowedByCurrentUser;

    private List<GuideResponse> guides;
    private List<UserListResponse> lists;
    private List<ReviewResponse> reviews;
}
