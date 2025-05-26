package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.model.Follow;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.notification.event.FollowedUserEvent;
import com.lorecodex.backend.repository.FollowRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.FollowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You can't follow yourself");
        }

        boolean alreadyFollowing = followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
        if (alreadyFollowing) return;

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        eventPublisher.publishEvent(new FollowedUserEvent(followingId, follower.getUsername()));
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public List<UserResponse> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(f -> toUserResponse(f.getFollower()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(f -> toUserResponse(f.getFollowing()))
                .collect(Collectors.toList());
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
