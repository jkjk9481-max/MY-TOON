package com.mytoon.backend.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// https://www.googleapis.com/oauth2/v3/userinfo 응답 모양
@JsonIgnoreProperties(ignoreUnknown = true)
record GoogleUserInfoResponse(
        String sub,     // 구글이 매긴 사용자 고유 ID
        String email,
        String name
) {
}
