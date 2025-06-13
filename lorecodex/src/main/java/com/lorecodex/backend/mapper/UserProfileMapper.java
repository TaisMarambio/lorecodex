package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.dto.response.ReviewResponse;
import com.lorecodex.backend.dto.response.UserListResponse;
import com.lorecodex.backend.dto.response.UserProfileResponse;
import com.lorecodex.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProfileMapper {

    public UserProfileResponse toProfileResponse(
            User user,
            boolean isFollowedByCurrentUser,
            int followersCount,
            int followingCount,
            List<GuideResponse> guides,
            List<UserListResponse> lists,
            List<ReviewResponse> reviews
    ) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .avatarUrl(null) // o user.getAvatarUrl(), chequear si agregamos que el usuario tenga una url de avatar
                .bio(null)       // por si en el futuro agregamos una bio
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowedByCurrentUser(isFollowedByCurrentUser)
                .guides(guides)
                .lists(lists)
                .reviews(reviews)
                .build();
    }
}
