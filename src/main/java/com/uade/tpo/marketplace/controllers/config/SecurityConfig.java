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
                                                .requestMatchers("/api/health", "/api/health/**", "/api/v1/auth/**", "/error", "/error/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                                                .requestMatchers("/api/v1/auth/**").permitAll()
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
                                                .requestMatchers(HttpMethod.GET, "/api/contents/**").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents").permitAll()
                                                .requestMatchers(HttpMethod.GET,"/api/contents/{id}").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                                                .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/network/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/network/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/analytics/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/analytics/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/reports/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/live/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/live/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/system/**").permitAll()
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS for all
                                                .requestMatchers("/ws/**").permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // Removed WebSecurityCustomizer to avoid MVC ignores masking issues
}

