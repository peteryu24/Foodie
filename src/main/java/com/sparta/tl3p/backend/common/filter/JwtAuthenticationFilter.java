package com.sparta.tl3p.backend.common.filter;

import com.sparta.tl3p.backend.common.util.JwtTokenProvider;
import com.sparta.tl3p.backend.domain.member.entity.CustomUserDetails;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
            String roleStr = jwtTokenProvider.getUserRoleFromToken(token);
            // Role enum로 변환 (토큰의 role 문자열과 enum명이 일치해야 합니다)
            Role role = Role.valueOf(roleStr);

            // CustomUserDetails의 추가 생성자를 사용하여 객체 생성
            CustomUserDetails customUserDetails = new CustomUserDetails(memberId, memberId.toString(), "", role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(customUserDetails, "", customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
