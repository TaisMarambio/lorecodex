package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.LoginRequest;
import com.lorecodex.backend.dto.request.RegisterRequest;
import com.lorecodex.backend.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse register(RegisterRequest request);

    JwtAuthenticationResponse login(LoginRequest request);
}