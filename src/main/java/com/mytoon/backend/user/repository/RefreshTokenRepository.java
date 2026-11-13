package com.mytoon.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.RefreshToken;
import com.mytoon.backend.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    // 클라이언트가 리프레시 토큰을 들고오면 , 그걸 해시해서 이 메서드로 DB의 해시값과 비교

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
    // 탈취 의심 시, 그 유저의 살아있는 Refresh Token을 전부 찾아서 한꺼번에 revoke() 처리하는 용도
}
