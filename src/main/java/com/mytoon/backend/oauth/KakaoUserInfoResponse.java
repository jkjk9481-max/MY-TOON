package com.mytoon.backend.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// https://kapi.kakao.com/v2/user/me 응답 모양 (필요한 필드만)
@JsonIgnoreProperties(ignoreUnknown = true)
record KakaoUserInfoResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record KakaoAccount(
            String email,
            Profile profile
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record Profile(
                String nickname
        ) {
        }
    }
}
