package com.sparta.tl3p.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Value("${jwt.refresh-header}")
    private String REFRESH_TOKEN_HEADER;

    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 저장 (key : memberId, Value : refreshToken)
    public void saveRefreshToken(Long memberId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_HEADER + memberId;
        redisTemplate.opsForValue().set(
                key, //key
                refreshToken, //value
                expirationTime,
                TimeUnit.MILLISECONDS
        );

    }
    // Refresh Token 조회
    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_HEADER + memberId);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_HEADER + memberId;
        redisTemplate.delete(key);


    }
}
