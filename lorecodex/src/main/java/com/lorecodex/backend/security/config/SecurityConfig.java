package com.lorecodex.backend.security.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.lorecodex.backend.security.jwt.JwtAuthenticationFilter;
import com.lorecodex.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private JwtAuthenticationFilter filter;
    private UserService userService;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter filter, UserService userService) {
        this.userService = userService;
        this.filter = filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults()) // Allow CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // Frontend compatibility endpoints
                        .requestMatchers("/games").permitAll()
                        .requestMatchers("/games/**").permitAll()
                        .requestMatchers("/admin/games").hasRole("ADMIN")
                        .requestMatchers("/admin/games/**").hasRole("ADMIN")

                        // Original API endpoints
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/games").permitAll()
                        .requestMatchers("/guides/**").hasRole("USER")
                        .requestMatchers("/api/games").permitAll()
                        .requestMatchers("/api/games/{id}").permitAll()
                        .requestMatchers("/api/games/{id}/like").permitAll()
                        .requestMatchers("/api/games/**").authenticated()

                        // Updated rating endpoints - require authentication
                        .requestMatchers("/api/games/{id}/rate").authenticated()
                        .requestMatchers("/api/games/{id}/rating").authenticated()

                        // Reviews endpoints
                        .requestMatchers("/api/reviews").permitAll()
                        .requestMatchers("/api/reviews/game/{gameId}").permitAll()
                        .requestMatchers("/api/reviews/{id}").permitAll()
                        .requestMatchers("/api/reviews/{id}/like").permitAll()
                        .requestMatchers("/api/reviews/{id}/dislike").permitAll()
                        .requestMatchers("/api/reviews/admin").hasRole("ADMIN")

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}