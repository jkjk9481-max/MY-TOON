package com.mytoon.backend.user.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.RefreshToken;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.entity.SocialAccount;
import com.mytoon.backend.user.entity.User;
import com.mytoon.backend.user.entity.UserStatus;
import com.mytoon.backend.user.repository.RefreshTokenRepository;
import com.mytoon.backend.user.repository.SocialAccountRepository;
import com.mytoon.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 30;

    private final UserRepository userRepository; // 회원 명부
    private final SocialAccountRepository socialAccountRepository; // "구글 / 카카오 ( 우리 회원 ) " 연결표
    private final RefreshTokenRepository refreshTokenRepository; // 발급한 출입 팔찌 기록
    private final JwtTokenProvider jwtTokenProvider; // Access Token(JWT) 발급기

    @Transactional
    public LoginResult loginOrRegister(Provider provider, String providerUserId, String email, String nickname) {
        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderUserId(provider, providerUserId) // 1.연결표에서 찾아봐
                .orElseGet(() -> createUserWithSocialAccount(provider, providerUserId, email, nickname)); // 2.없으면 새로 만든다
        // "있으면 그걸 쓰고 , 없으면(orElse) 이걸 해(Get)
        // 연결표에 있으면 → "아, 단골이네" → 기존 회원 정보 사용 (로그인)
        //- 없으면 → "처음 오셨네" → createUserWithSocialAccount로 회원증 새로 발급 ( 회원 가입 )

        User user = socialAccount.getUser(); // 회원증에 적힌 '사람'을 꺼냄
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole()); // 오늘 쓸 '신분증' 발급
        String rawRefreshToken = issueRefreshToken(user); // 오늘 쓸 '출입 팔찌' 발급

        return new LoginResult(user, accessToken, rawRefreshToken); // 회원 + 신분증 + 팔찌를 봉투에 담아 돌려줌
    }

    private SocialAccount createUserWithSocialAccount(
            Provider provider, String providerUserId, String email, String nickname) {
        User user = userRepository.save(
                User.builder() // 1. 회원 명부에 등록
                        .nickname(nickname)
                        .email(email)
                        .role(Role.USER)
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        // 2.연결표에 기록
        return socialAccountRepository.save(
                SocialAccount.builder()
                        .user(user)
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .build()
        );
    }

    private String issueRefreshToken(User user) {
        String rawToken = generateRawToken(); // 1. 아무도 못 맞추는 랜덤 팔찌번호 생성

        refreshTokenRepository.save( // 2.근데 서랍엔 '원본'말고 '변형본'만 저장
                RefreshToken.builder()
                        .user(user)
                        .tokenHash(hash(rawToken))
                        .expiresAt(Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS))
                        .build()
        );

        return rawToken; // 3. 원본은 손님에게 직접 줌
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
        // 우리(서버)는 변형본(해시)밖에 안 가지고 있어서, 원본을 되돌릴 방법이 없습니다.
        // 원본이 존재하는 유일한 순간이 "방금 막 만든 지금"뿐이다
        // 핵심 : 해시는 복원불가라 지금 밖에 기회가없다
    }
}
