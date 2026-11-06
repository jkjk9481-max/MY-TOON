package com.mytoon.backend.user.controller.dto;

import com.mytoon.backend.user.entity.Provider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 임시 로그인 엔드포인트의 요청 바디.
 * 원래 OAuth2가 구글/카카오에서 받아 넘겨줄 값들을, 지금은 클라이언트가 직접 채워 보낸다.
 */
public record LoginRequest(
        @NotNull Provider provider,        // GOOGLE / KAKAO (필수)  , 값이 null이면 안됨
        @NotBlank String providerUserId,   // 소셜이 매긴 고유 식별자 (필수) , 문자열이 null*빈칸*공백뿐이면 안됨
        String email,                      // 소셜이 안 줄 수도 있어 선택
        String nickname                    // 선택
) {
}
