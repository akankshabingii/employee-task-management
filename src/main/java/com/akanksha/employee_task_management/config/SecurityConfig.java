package com.akanksha.employee_task_management.config;

import com.akanksha.employee_task_management.security.JwtAuthenticationFilter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                JwtAuthenticationFilter jwtAuthenticationFilter) {

            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                HttpSecurity http) throws Exception {

            http
                    .csrf(csrf -> csrf.disable())

                    .sessionManagement(session ->
                            session.sessionCreationPolicy(
                                    SessionCreationPolicy.STATELESS
                            )
                    )

                    .authorizeHttpRequests(auth -> auth

                            // =========================
                            // LOGIN
                            // =========================

                            .requestMatchers("/auth/login")
                            .permitAll()

                            // =========================
                            // USERS
                            // =========================

                            // Get all users → ADMIN only
                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/users"
                            )
                            .hasRole("ADMIN")

                            // Create user → ADMIN only
                            .requestMatchers(
                                    HttpMethod.POST,
                                    "/users"
                            )
                            .hasRole("ADMIN")

                            // Update user → ADMIN only
                            .requestMatchers(
                                    HttpMethod.PUT,
                                    "/users/**"
                            )
                            .hasRole("ADMIN")

                            // Delete user → ADMIN only
                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    "/users/**"
                            )
                            .hasRole("ADMIN")

                            // Individual user GET endpoints
                            // Ownership is checked in UserController
                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/users/**"
                            )
                            .authenticated()

                            // =========================
                            // TEAMS
                            // =========================

                            .requestMatchers(
                                    HttpMethod.POST,
                                    "/teams/**"
                            )
                            .hasRole("ADMIN")

                            .requestMatchers(
                                    HttpMethod.PUT,
                                    "/teams/**"
                            )
                            .hasRole("ADMIN")

                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    "/teams/**"
                            )
                            .hasRole("ADMIN")

                            .requestMatchers("/teams/**")
                            .authenticated()

                            // =========================
                            // TASK DELETE
                            // =========================

                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    "/tasks/**"
                            )
                            .hasRole("ADMIN")

                            // =========================
                            // TASKS
                            // =========================

                            .requestMatchers("/tasks/**")
                            .authenticated()

                            // =========================
                            // EVERYTHING ELSE
                            // =========================

                            .anyRequest()
                            .authenticated()
                    )

                    .addFilterBefore(
                            jwtAuthenticationFilter,
                            UsernamePasswordAuthenticationFilter.class
                    );

            return http.build();
        }

    }