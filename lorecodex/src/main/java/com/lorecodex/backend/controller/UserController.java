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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user, Authentication authentication) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserResponse dto = userMapper.toDTO(user);
        if ((dto.getRoles() == null || dto.getRoles().isEmpty()) && authentication != null) {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .toList();
            dto.setRoles(roles);
        }
        return ResponseEntity.ok(dto);
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

    @PatchMapping("/{userId}/username")
    public ResponseEntity<UserResponse> changeUsername(
            @PathVariable Long userId,
            @RequestBody() Map<String, String> body,
            @AuthenticationPrincipal User currentUser,
            Authentication authentication
    ) {
        String newUsername = body.get("username");
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
        boolean isSelf = currentUser != null && currentUser.getId().equals(userId);

        if (!isAdmin && !isSelf) {
            return ResponseEntity.status(403).build();
        }

        User updated = userService.updateUsername(userId, newUsername.trim());
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @GetMapping("/username-available/{candidate}")
    public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable String candidate) {
        String normalized = candidate == null ? "" : candidate.trim();
        if (normalized.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        boolean available = userService.getUserByUsername(normalized).isEmpty();
        return ResponseEntity.ok(available);
    }
}
