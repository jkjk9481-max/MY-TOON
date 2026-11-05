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

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResult loginOrRegister(Provider provider, String providerUserId, String email, String nickname) {
        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> createUserWithSocialAccount(provider, providerUserId, email, nickname));

        User user = socialAccount.getUser();
        String rawRefreshToken = issueRefreshToken(user);

        return new LoginResult(user, rawRefreshToken);
    }

    private SocialAccount createUserWithSocialAccount(
            Provider provider, String providerUserId, String email, String nickname) {
        User user = userRepository.save(
                User.builder()
                        .nickname(nickname)
                        .email(email)
                        .role(Role.USER)
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        return socialAccountRepository.save(
                SocialAccount.builder()
                        .user(user)
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .build()
        );
    }

    private String issueRefreshToken(User user) {
        String rawToken = generateRawToken();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .tokenHash(hash(rawToken))
                        .expiresAt(Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS))
                        .build()
        );

        return rawToken;
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
    }
}
