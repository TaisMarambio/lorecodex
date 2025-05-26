package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.response.UserResponse;

import java.util.List;

public interface FollowService {

    void followUser(Long followerId, Long followingId);

    void unfollowUser(Long followerId, Long followingId);

    boolean isFollowing(Long followerId, Long followingId);

    List<UserResponse> getFollowers(Long userId);

    List<UserResponse> getFollowing(Long userId);
}
