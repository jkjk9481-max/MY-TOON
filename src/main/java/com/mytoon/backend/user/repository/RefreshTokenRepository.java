package com.mytoon.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    // 클라이언트가 리프레시 토큰을 들고오면 , 그걸 해시해서 이 메서드로 DB의 해시값과 비교
}
