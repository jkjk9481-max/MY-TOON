package com.mytoon.backend.user.controller.dto;

import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.service.LoginResult;

/**
 * 임시 로그인 엔드포인트의 응답 바디.
 * 로그인/가입이 실제로 처리됐는지 눈으로 확인하는 용도라, 누가 로그인됐는지와 발급된 토큰 원문을 담는다.
 */
public record LoginResponse(
        Long userId,
        String nickname,
        Role role,
        String refreshToken
) {
    // 서비스 결과(LoginResult) -> 응답 DTO 변환. 엔티티를 그대로 노출하지 않으려고 한 번 거른다.
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.user().getId(),
                result.user().getNickname(),
                result.user().getRole(),
                result.refreshToken()
        );
    }
}
