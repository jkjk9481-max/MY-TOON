package com.mytoon.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(Provider provider, String providerUserId);
    // "소셜 로그인 시 이 Provider , providerUserId 조합의 계정이 이미 있는지 찾는 핵심 조회
}
