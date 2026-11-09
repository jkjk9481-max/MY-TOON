package com.mytoon.backend.oauth;

import com.mytoon.backend.user.entity.Provider;

// 심부름 다녀온 결과를 담은 봉투
public record OAuth2UserInfo(
        Provider provider,
        String providerUserId,
        String email,
        String nickname
) {
}
