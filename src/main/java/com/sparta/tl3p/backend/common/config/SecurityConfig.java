package com.sparta.tl3p.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        //                        .requestMatchers("/api/v1/members/signup").permitAll()
                        .requestMatchers("/api/**").permitAll()        // 개발 중 api 테스트를 위해 모든 요청에 대한 인증 절차 생략
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // springdoc-openapi
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}