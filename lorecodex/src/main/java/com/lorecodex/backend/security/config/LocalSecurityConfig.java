package com.lorecodex.backend.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.lorecodex.backend.security.jwt.JwtAuthenticationFilter;
import com.lorecodex.backend.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "security.auth.provider", havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalSecurityConfig  {

    private final JwtAuthenticationFilter filter;
    private final UserService userService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Frontend compatibility endpoints
                        .requestMatchers("/admin/games").hasRole("ADMIN")
                        .requestMatchers("/admin/games/**").hasRole("ADMIN")

                        // Original API endpoints
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        //.requestMatchers("/guides/published").permitAll()
                        //.requestMatchers("/guides/{id}").permitAll()
                        //.requestMatchers("/guides/**").hasRole("USER")
                        .requestMatchers("/games").permitAll()
                        .requestMatchers("/games/allGames").permitAll()
                        .requestMatchers("/games/{id}").permitAll()
                        .requestMatchers("/games/{id}/like").permitAll()

                        // BATCH IMPORT - IMPORTANTE: Debe ir ANTES de /games/**
                        .requestMatchers("/games/batch/import").hasRole("ADMIN")

                        //GameNotes endpoints
                        .requestMatchers("/notes/**").authenticated()

                        //non-required authentication
                        .requestMatchers("/games/{id}/average-rating").permitAll()

                        // Reviews endpoints
                        .requestMatchers("/reviews/**").permitAll()

                        //Rating endpoints
                        .requestMatchers("/rating/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        //igdb
                        .requestMatchers("/igdb/**").permitAll()

                        //listas
                        .requestMatchers("/lists/**").permitAll()

                        //guides
                        .requestMatchers("/guides/**").permitAll()
                        .requestMatchers("/follow/**").authenticated()

                        //challenges
                        .requestMatchers("/challenges/**").permitAll()

                        //news
                        .requestMatchers("/news/**").permitAll()

                        .requestMatchers("/notifications/**").authenticated()
                        .requestMatchers("/comments/**").permitAll()
                        .requestMatchers("/test-email/**").permitAll()
                        .requestMatchers("/settings/**").permitAll()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
