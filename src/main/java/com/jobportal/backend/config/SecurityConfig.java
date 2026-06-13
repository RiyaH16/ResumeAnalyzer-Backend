package com.jobportal.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import com.jobportal.backend.security.JwtFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(cors -> {})

            .csrf(csrf -> csrf.disable())

            .headers(headers ->
                headers.frameOptions(
                    frame -> frame.disable()
                )
            )

            .sessionManagement(sess ->
                sess.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                	"/",
                    "/error",
                    "/auth/**"
                    
                ).permitAll()

                .requestMatchers(
                        "/resume/**"
                    ).authenticated()

                    .requestMatchers(
                        "/user/**"
                    ).authenticated()

                .anyRequest()
                .authenticated()
            )

            .httpBasic(
                httpBasic -> httpBasic.disable()
            )
            
            .formLogin(
                form -> form.disable()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}