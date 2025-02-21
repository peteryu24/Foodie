package com.sparta.tl3p.backend.common.filter;

import com.sparta.tl3p.backend.common.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
            UserDetails userDetails = User.withUsername(memberId.toString()).password("").authorities("ROLE_USER").build();
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(userDetails));
        }

        chain.doFilter(request, response);
    }


}
