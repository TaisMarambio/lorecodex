package com.lorecodex.backend.security.jwt;

import com.lorecodex.backend.service.JwtService;
import com.lorecodex.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro híbrido que soporta tanto JWT tradicional como Auth0
 * Solo se activa para tokens JWT tradicionales (no Auth0)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip for public auth endpoints
        if (path.startsWith("/auth/") || path.startsWith("/auth0/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Check if it's an Auth0 token (they contain dots and are longer)
        // Auth0 JWTs have a specific format, traditional JWTs from our app are different
        if (isAuth0Token(jwt)) {
            log.debug("Detected Auth0 token, skipping traditional JWT filter");
            filterChain.doFilter(request, response);
            return;
        }

        // Process traditional JWT
        try {
            final String userIdentifier = jwtService.extractUserName(jwt);

            if (StringUtils.hasText(userIdentifier) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails;
                try {
                    userDetails = userService.loadUserByUsername(userIdentifier);
                } catch (UsernameNotFoundException e) {
                    userDetails = userService.loadUserByEmail(userIdentifier);
                }

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                    log.debug("Traditional JWT authenticated for user: {}", userIdentifier);
                }
            }
        } catch (Exception e) {
            log.debug("Traditional JWT validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuth0Token(String token) {
        // Auth0 tokens are typically much longer and have specific claims
        // Simple heuristic: Auth0 JWTs are usually > 500 characters
        // and contain specific patterns
        if (token.length() < 200) {
            return false; // Probably our traditional JWT
        }

        // Try to decode and check for Auth0-specific claims
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            // If token is very long, it's likely Auth0
            return token.length() > 400;
        } catch (Exception e) {
            return false;
        }
    }
}