package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.request.RegisterRequest;
import com.lorecodex.backend.dto.response.JwtAuthenticationResponse;
import com.lorecodex.backend.repository.RoleRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.model.Role;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.dto.request.LoginRequest;
import com.lorecodex.backend.service.AuthenticationService;
import com.lorecodex.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Override
    public JwtAuthenticationResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    log.warn("ROLE_USER not found. Creating it automatically.");
                    return roleRepository.save(new Role(null, "ROLE_USER", null));
                });

        //create and save user
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        //generate JWT and return response
        return createJwtResponse(user);
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password.");
        }

        log.info("User logged in: {}", user.getUsername());

        return createJwtResponse(user);  //generate JWT and return response
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    private JwtAuthenticationResponse createJwtResponse(User user) {
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .build();
    }
}