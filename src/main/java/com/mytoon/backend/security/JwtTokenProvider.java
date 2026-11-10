package com.mytoon.backend.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mytoon.backend.user.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    // "도장이 찍힌 신분증"을 만들고
    // 나중에 그 도장이 진짜인지 확인하는 역할

    private static final long ACCESS_TOKEN_VALIDITY_HOURS = 1;
    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey; // 도장

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 신분증 발급
    public String generateAccessToken(Long userId, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(ROLE_CLAIM, role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ACCESS_TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String accessToken) {
        return Long.parseLong(parseClaims(accessToken).getSubject());
    }

    public Role getRole(String accessToken) {
        return Role.valueOf(parseClaims(accessToken).get(ROLE_CLAIM, String.class));
    }

    // 신분증 보고 도장이 진짜인지 확인 + 적힌 내용 읽기
    public boolean validateToken(String accessToken) {
        try {
            parseClaims(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 도장 확인하고 봉투 뜯기
    private Claims parseClaims(String accessToken) {
        return Jwts.parser()
                .verifyWith(secretKey) // "이 토큰에 찍힌 도장을 , 우리 비밀 도장으로 다시 검사해봐"
                .build()
                .parseSignedClaims(accessToken) // 실제로 토큰 문자열을 넣어서 도장이 맞는지 검사하고 , 맞으면 안의 내용을 꺼냄 안맞으면 에러
                .getPayload(); // 검사를 통과했을때 , 안에 들어있던 내용을꺼냄
    }
}
