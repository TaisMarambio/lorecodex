package com.lorecodex.backend.security.auth0;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class Auth0JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Auth0Properties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

    public Auth0JwtAuthenticationConverter(
            Auth0Properties properties,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        this.authoritiesConverter = jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>(scopesConverter.convert(jwt));
            authorities.addAll(extractPermissions(jwt));
            authorities.addAll(extractRoles(jwt));
            return authorities;
        };
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);

        if (!properties.autoProvisionUser()) {
            return new UsernamePasswordAuthenticationToken(jwt, "n/a", authorities);
        }

        User user = resolveOrCreateUser(jwt);
        return new UsernamePasswordAuthenticationToken(user, "n/a", authorities);
    }

    private Collection<GrantedAuthority> extractPermissions(Jwt jwt) {
        List<String> permissions = claimAsStringList(jwt, properties.permissionsClaim());
        if (permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream()
                .filter(StringUtils::hasText)
                .map(p -> (GrantedAuthority) new SimpleGrantedAuthority("PERMISSION_" + p))
                .toList();
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        List<String> roles = claimAsStringList(jwt, properties.rolesClaim());
        if (roles.isEmpty()) {
            return List.of();
        }

        return roles.stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeRoleAuthority)
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }

    private String normalizeRoleAuthority(String role) {
        String trimmed = role.trim();
        String upper = trimmed.toUpperCase();
        if (trimmed.startsWith("ROLE_")) {
            return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
        }
        return "ROLE_" + upper;
    }

    private List<String> claimAsStringList(Jwt jwt, String claimNames) {
        if (!StringUtils.hasText(claimNames)) {
            return List.of();
        }

        for (String claimName : claimNames.split(",")) {
            String trimmed = claimName.trim();
            if (trimmed.isBlank()) {
                continue;
            }

            Object claim = jwt.getClaims().get(trimmed);
            if (claim instanceof List<?> list) {
                List<String> values = list.stream().filter(Objects::nonNull).map(Object::toString).toList();
                if (!values.isEmpty()) {
                    return values;
                }
            }
            if (claim instanceof String s && StringUtils.hasText(s)) {
                return List.of(s);
            }
        }

        return List.of();
    }

    private User resolveOrCreateUser(Jwt jwt) {
        String principalClaim = StringUtils.hasText(properties.principalClaim()) ? properties.principalClaim() : "sub";
        String username = firstNonBlank(jwt.getClaimAsString(principalClaim), jwt.getSubject());
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("JWT does not contain a usable principal (sub/claim missing)");
        }

        String emailFromToken = jwt.getClaimAsString(properties.emailClaim());
        final String email = StringUtils.hasText(emailFromToken) ? emailFromToken : syntheticEmail(username);

        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(email))
                .orElseGet(() -> createUser(username, email));
    }

    private User createUser(String username, String email) {
        try {
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("AUTH0"))
                    .roles(new HashSet<>())
                    .build();
            return userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            return userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(email))
                    .orElseThrow(() -> e);
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a;
        }
        return b;
    }

    private static String syntheticEmail(String stableId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(stableId.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return "auth0+" + hex.substring(0, 16) + "@lorecodex.local";
        } catch (Exception e) {
            return "auth0+" + stableId.replaceAll("[^a-zA-Z0-9]", "") + "@lorecodex.local";
        }
    }
}
