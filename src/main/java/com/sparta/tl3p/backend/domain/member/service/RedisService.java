package com.sparta.tl3p.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {


    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 저장 (key : memberId, Value : refreshToken)
    public void saveRefreshToken(Long memberId, String refreshToken, long expirationTime) {
        String key = "refresh:" + memberId;
        redisTemplate.opsForValue().set(
                key, //key
                refreshToken, //value
                expirationTime,
                TimeUnit.MILLISECONDS
        );

    }
    // Refresh Token 조회
    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get("refresh:" + memberId);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long memberId) {
        String key = "refresh:" + memberId;
        redisTemplate.delete(key);


    }
}
