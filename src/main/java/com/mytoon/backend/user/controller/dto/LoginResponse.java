package com.mytoon.backend.user.controller.dto;

import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.service.LoginResult;

public record LoginResponse(
        Long userId,
        String nickname,
        Role role,
        String accessToken,
        String refreshToken
) {
    // 서비스 결과(LoginResult) -> 응답 DTO 변환. 엔티티를 그대로 노출하지 않으려고 한 번 거른다.
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.user().getId(),
                result.user().getNickname(),
                result.user().getRole(),
                result.accessToken(),
                result.refreshToken()
        );
    }
}
