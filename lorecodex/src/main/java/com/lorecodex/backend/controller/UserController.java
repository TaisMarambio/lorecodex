package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.GuideResponse;
import com.lorecodex.backend.dto.response.UserProfileResponse;
import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.mapper.UserMapper;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.service.GuideService;
import com.lorecodex.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;
    private final GuideService guideService;
    private final UserService userService;

    @Autowired
    public UserController(UserMapper userMapper, GuideService guideService, UserService userService) {
        this.userMapper = userMapper;
        this.guideService = guideService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/my-drafts")
    public ResponseEntity<List<GuideResponse>> getMyDrafts(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<GuideResponse> drafts = guideService.getDraftsByUserId(user.getId());
        return ResponseEntity.ok(drafts);
    }

    //ver el perfil de un usuario por su id
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(userService.getUserProfileById(userId, currentUser.getId()));
    }


}