package com.lorecodex.backend.controller;

import com.lorecodex.backend.dto.response.UserResponse;
import com.lorecodex.backend.mapper.UserMapper;
import com.lorecodex.backend.model.Role;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.RoleRepository;
import com.lorecodex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth0")
@CrossOrigin("*")
@RequiredArgsConstructor
public class Auth0Controller {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String nickname = jwt.getClaimAsString("nickname");

        log.info("Auth0 user accessing /me - auth0Id: {}, email: {}", auth0Id, email);

        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    log.info("Creating new user from Auth0: {}", email);
                    return createUserFromAuth0(auth0Id, email, name, nickname);
                });

        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String nickname = jwt.getClaimAsString("nickname");

        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> createUserFromAuth0(auth0Id, email, name, nickname));

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMapper.toDTO(user));
        response.put("message", "User synced successfully");

        return ResponseEntity.ok(response);
    }

    private User createUserFromAuth0(String auth0Id, String email, String name, String nickname) {
        // Validar que tengamos al menos un identificador
        if (email == null && nickname == null && name == null) {
            throw new IllegalArgumentException("Cannot create user: no email, nickname, or name provided by Auth0");
        }

        // Determinar username con fallbacks
        String username;
        if (nickname != null && !nickname.trim().isEmpty()) {
            username = nickname;
        } else if (email != null && !email.trim().isEmpty()) {
            username = email.split("@")[0];
        } else if (name != null && !name.trim().isEmpty()) {
            username = name.replaceAll("\\s+", "_"); // Reemplazar espacios con guiones bajos
        } else {
            // Último recurso: usar una parte del auth0Id
            username = "user_" + auth0Id.substring(auth0Id.lastIndexOf("|") + 1, Math.min(auth0Id.lastIndexOf("|") + 9, auth0Id.length()));
        }

        // Si el username ya existe, agregar un sufijo
        String finalUsername = username;
        int suffix = 1;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = username + suffix;
            suffix++;
        }

        // Obtener rol USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    log.warn("ROLE_USER not found. Creating it automatically.");
                    return roleRepository.save(new Role(null, "ROLE_USER", null));
                });

        User newUser = User.builder()
                .auth0Id(auth0Id)
                .username(finalUsername)
                .email(email != null ? email : auth0Id + "@auth0.local") // Email fallback
                .isAuth0User(true)
                .password(null) // No password for Auth0 users
                .roles(Set.of(userRole))
                .emailNotificationsEnabled(email != null) // Solo habilitar si tiene email real
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Created new Auth0 user: {} with username: {}", email, finalUsername);

        return savedUser;
    }
}
