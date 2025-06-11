package com.lorecodex.backend.security.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.lorecodex.backend.security.jwt.JwtAuthenticationFilter;
import com.lorecodex.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
                        .requestMatchers("settings/**").permitAll()
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