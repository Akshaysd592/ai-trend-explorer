package com.aitrend.trend.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for trend-service.
 *
 * Access Rules:
 * - GET   /api/v1/trends/**          -> Public (no auth required - frontend reads freely)
 * - PATCH /api/v1/trends/{id}/ai-metadata -> Public / Inter-service communication from ai-analysis-service
 * - POST  /api/v1/trends/ingest      -> Protected (requires valid JWT - admin operation)
 * - POST  /api/v1/trends             -> Protected (requires valid JWT)
 * - Swagger UI / actuator            -> Public
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — stateless REST API uses JWT, not cookies
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session — no HttpSession created
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                // Swagger UI & OpenAPI docs — always public
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).permitAll()

                // Actuator health endpoint — always public
                .requestMatchers("/actuator/**").permitAll()

                // Read trends — public (frontend displays without login)
                .requestMatchers(HttpMethod.GET, "/api/v1/trends/**").permitAll()

                // Inter-service update for AI metadata
                .requestMatchers(HttpMethod.PATCH, "/api/v1/trends/**").permitAll()

                // Write/admin operations — require valid JWT
                .requestMatchers(HttpMethod.POST, "/api/v1/trends/ingest").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/trends").authenticated()

                // Everything else — require auth
                .anyRequest().authenticated()
            )

            // Register JWT filter before Spring's UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
