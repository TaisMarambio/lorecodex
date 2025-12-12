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
 * Filtro que maneja JWT tradicionales (no Auth0)
 * Solo procesa tokens cortos que son generados por nuestra aplicación
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

        // Skip para endpoints públicos de auth
        if (path.startsWith("/auth/") || path.startsWith("/auth0/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Si no hay header, continuar
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Si ya hay autenticación en el contexto, no hacer nada
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificar si es un token Auth0 (son mucho más largos)
        if (isAuth0Token(jwt)) {
            log.debug("Detected Auth0 token, skipping traditional JWT filter for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Procesar JWT tradicional
        try {
            final String userIdentifier = jwtService.extractUserName(jwt);

            if (StringUtils.hasText(userIdentifier)) {
                UserDetails userDetails;
                try {
                    userDetails = userService.loadUserByUsername(userIdentifier);
                } catch (UsernameNotFoundException e) {
                    // Intentar por email
                    try {
                        userDetails = userService.loadUserByEmail(userIdentifier);
                    } catch (UsernameNotFoundException ex) {
                        log.debug("User not found: {}", userIdentifier);
                        filterChain.doFilter(request, response);
                        return;
                    }
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
                    log.debug("Traditional JWT authenticated for user: {} on path: {}", userIdentifier, path);
                }
            }
        } catch (Exception e) {
            log.debug("Traditional JWT validation failed for path {}: {}", path, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determina si un token es de Auth0 basado en su longitud y estructura
     * Los tokens Auth0 son típicamente > 500 caracteres
     * Los tokens JWT tradicionales de nuestra app son más cortos (< 300 caracteres)
     */
    private boolean isAuth0Token(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // Los tokens Auth0 son mucho más largos
        if (token.length() < 300) {
            return false; // Probablemente nuestro JWT tradicional
        }

        // Verificar estructura básica JWT (3 partes separadas por puntos)
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            // Si el token es muy largo, es probablemente Auth0
            return token.length() > 400;
        } catch (Exception e) {
            log.debug("Error checking token type: {}", e.getMessage());
            return false;
        }
    }
}
