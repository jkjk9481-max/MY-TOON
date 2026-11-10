package com.mytoon.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mytoon.backend.user.entity.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtTokenProviderTest {

    // HS256은 최소 32바이트 키가 필요해서 테스트용 시크릿도 그 길이를 맞춤
    private static final String SECRET = "test-secret-key-for-jwt-unit-test-32bytes!!";

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET);

    @Test
    @DisplayName("Access Token을 발급하면 담았던 userId와 role을 그대로 꺼낼 수 있다")
    void generateAccessToken_thenParseBack() {
        String token = jwtTokenProvider.generateAccessToken(1L, Role.USER);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(1L);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(Role.USER);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("서명이 깨진(위조된) 토큰은 검증에 실패한다")
    void validateToken_tamperedToken_returnsFalse() {
        String token = jwtTokenProvider.generateAccessToken(1L, Role.USER);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    @DisplayName("만료 시각이 지난 토큰은 검증에 실패한다")
    void validateToken_expiredToken_returnsFalse() {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        String expiredToken = Jwts.builder()
                .subject("1")
                .claim("role", Role.USER.name())
                .issuedAt(Date.from(now.minus(2, ChronoUnit.HOURS)))
                .expiration(Date.from(now.minus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();

        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("JWT 형식이 아닌 문자열은 검증에 실패한다")
    void validateToken_malformedString_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("not-a-jwt-token")).isFalse();
    }
}
