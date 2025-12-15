package com.lorecodex.backend.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.security.auth0.AudienceValidator;
import com.lorecodex.backend.security.auth0.Auth0JwtAuthenticationConverter;
import com.lorecodex.backend.security.auth0.Auth0Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "security.auth.provider", havingValue = "auth0")
@EnableConfigurationProperties(Auth0Properties.class)
public class Auth0SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            JwtDecoder jwtDecoder,
            Auth0JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Keep existing public routes
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/games").permitAll()
                        .requestMatchers("/games/allGames").permitAll()
                        .requestMatchers("/games/{id}").permitAll()
                        .requestMatchers("/games/{id}/like").permitAll()
                        .requestMatchers("/games/{id}/average-rating").permitAll()
                        .requestMatchers("/reviews/**").permitAll()
                        .requestMatchers("/rating/**").permitAll()
                        .requestMatchers("/igdb/**").permitAll()
                        .requestMatchers("/lists/**").permitAll()
                        .requestMatchers("/guides/**").permitAll()
                        .requestMatchers("/challenges/**").permitAll()
                        .requestMatchers("/news/**").permitAll()
                        .requestMatchers("/comments/**").permitAll()
                        .requestMatchers("/test-email/**").permitAll()
                        .requestMatchers("/settings/**").permitAll()

                        // Protected routes
                        .requestMatchers("/admin/games").hasRole("ADMIN")
                        .requestMatchers("/admin/games/**").hasRole("ADMIN")
                        .requestMatchers("/games/batch/import").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/notes/**").authenticated()
                        .requestMatchers("/follow/**").authenticated()
                        .requestMatchers("/notifications/**").authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(Auth0Properties properties) {
        String issuer = StringOrDefault.firstNonBlank(properties.issuerUri(), defaultIssuer(properties.domain()));
        String jwksUri = StringOrDefault.firstNonBlank(properties.jwksUri(), defaultJwks(properties.domain()));

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(properties.audience());
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));

        return jwtDecoder;
    }

    @Bean
    public Auth0JwtAuthenticationConverter jwtAuthenticationConverter(
            Auth0Properties properties,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new Auth0JwtAuthenticationConverter(properties, userRepository, passwordEncoder);
    }

    private static String defaultIssuer(String domain) {
        String base = normalizeAuth0BaseUrl(domain);
        return base.endsWith("/") ? base : base + "/";
    }

    private static String defaultJwks(String domain) {
        String base = normalizeAuth0BaseUrl(domain);
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base) + "/.well-known/jwks.json";
    }

    private static String normalizeAuth0BaseUrl(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalStateException("Missing Auth0 domain configuration (auth0.domain / AUTH0_DOMAIN)");
        }
        String trimmed = domain.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return "https://" + trimmed;
    }

    private static final class StringOrDefault {
        private static String firstNonBlank(String value, String fallback) {
            return (value != null && !value.isBlank()) ? value : fallback;
        }
    }
}
