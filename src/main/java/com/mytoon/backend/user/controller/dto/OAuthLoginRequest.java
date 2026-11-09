package com.mytoon.backend.user.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank String code // 구글/카카오 인가 서버가 프론트로 내려준 인가 코드
) {
}
