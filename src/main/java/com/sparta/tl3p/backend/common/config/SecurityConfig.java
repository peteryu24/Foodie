package com.sparta.tl3p.backend.common.config;

import com.sparta.tl3p.backend.common.filter.JwtAuthenticationFilter;
import com.sparta.tl3p.backend.common.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/members/signup", "/api/v1/members/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/members/{id}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/members/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/members").hasAnyRole("MASTER", "MANAGER")

                        // store
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores").permitAll() // (모든 사용자)
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}").permitAll() // (모든 사용자)
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/scores").permitAll() // (모든 사용자)

                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/owner/stores").hasRole("OWNER") // 점주만
                        .requestMatchers(HttpMethod.POST, "/api/v1/stores").hasRole("OWNER") // 점주만
                        .requestMatchers(HttpMethod.PUT, "/api/v1/stores/{storeId}").hasRole("OWNER") // 점주만
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/stores/{storeId}").hasRole("OWNER") // 점주만
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/{storeId}").hasRole("OWNER") // 점주만

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
