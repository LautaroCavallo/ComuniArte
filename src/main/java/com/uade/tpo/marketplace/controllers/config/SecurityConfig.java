package com.uade.tpo.marketplace.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.uade.tpo.marketplace.entity.Rol;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
@RequiredArgsConstructor
public class SecurityConfig {

        private final JWTAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //configuro seguridad http
                http
                        .cors(cors -> cors.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOriginPatterns(List.of("*"));
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(List.of("*"));
                        config.setAllowCredentials(true);
                        return config;
                        }))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                                .requestMatchers("/api/health", "/api/health/**", "/api/v1/auth/**", "/error", "/error/**").permitAll()                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/contents").permitAll()
                                                .requestMatchers(HttpMethod.PUT,"/api/contents/{id}").permitAll()
                                                .requestMatchers(HttpMethod.DELETE,"/api/contents/{id}").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/contents/{id}/view").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/contents/{id}/comments").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents/{id}/comments").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents/{id}/likes").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/contents/{id}/like").permitAll()
                                                .requestMatchers(HttpMethod.DELETE,"/api/contents/{id}/like/**").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents/{id}/liked/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/contents/list/**").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents/{id}").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/network/follow/**").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/users/login").permitAll()
    .requestMatchers(HttpMethod.OPTIONS, "/api/users/**").permitAll() // Added OPTIONS
                                                .requestMatchers("/ws/**").permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // Removed WebSecurityCustomizer to avoid MVC ignores masking issues
}

