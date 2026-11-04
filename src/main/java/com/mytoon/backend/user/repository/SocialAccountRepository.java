package com.mytoon.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
