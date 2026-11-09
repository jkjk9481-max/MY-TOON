package com.mytoon.backend.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.mytoon.backend.user.entity.Provider;

@Component
public class KakaoOAuth2Client implements OAuth2Client {

    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USERINFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public KakaoOAuth2Client(
            RestClient.Builder restClientBuilder,
            @Value("${oauth2.kakao.client-id}") String clientId,
            @Value("${oauth2.kakao.client-secret}") String clientSecret,
            @Value("${oauth2.kakao.redirect-uri}") String redirectUri) {
        this.restClient = restClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    @Override
    public Provider provider() {
        return Provider.KAKAO;
    }

    @Override
    public OAuth2UserInfo fetchUserInfo(String code) {
        String accessToken = exchangeCodeForAccessToken(code);
        KakaoUserInfoResponse userInfo = fetchKakaoUserInfo(accessToken);

        String providerUserId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
        String nickname = userInfo.kakaoAccount() != null && userInfo.kakaoAccount().profile() != null
                ? userInfo.kakaoAccount().profile().nickname()
                : null;

        return new OAuth2UserInfo(Provider.KAKAO, providerUserId, email, nickname);
    }

    private String exchangeCodeForAccessToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        KakaoTokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KakaoTokenResponse.class);

        return tokenResponse.accessToken();
    }

    private KakaoUserInfoResponse fetchKakaoUserInfo(String accessToken) {
        return restClient.get()
                .uri(USERINFO_URI)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
}
