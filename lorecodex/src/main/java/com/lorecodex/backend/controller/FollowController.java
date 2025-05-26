package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    //seguir a un usuario
    @PostMapping("/{followerId}/follow/{followingId}")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId
    ) {
        followService.followUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    //dejar de seguir a un usuario
    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId
    ) {
        followService.unfollowUser(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    //consulta si un usuario sigue a otro
    @GetMapping("/{followerId}/is-following/{followingId}")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long followerId,
            @PathVariable Long followingId
    ) {
        return ResponseEntity.ok(followService.isFollowing(followerId, followingId));
    }

    //obtener seguidores de un usuario
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    //obtener a quién sigue un usuario
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
