package com.lorecodex.backend.controller;

import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserRepository userRepository;

    @PatchMapping("/email-notifications")
    public ResponseEntity<Void> updateEmailNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam boolean enabled
    ) {
        user.setEmailNotificationsEnabled(enabled);
        userRepository.save(user);
        return ResponseEntity.ok().build();   //cambiarrrrrr, ta horrible
    }
}
