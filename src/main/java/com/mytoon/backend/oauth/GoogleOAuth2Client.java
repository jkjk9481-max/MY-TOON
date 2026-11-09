package com.mytoon.backend.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.mytoon.backend.user.entity.Provider;

@Component
// 구글 전담 통역사 -> 구글한테 이 code로 토큰줘 ->  그 토큰으로 "이 사람 누구야" 두 번 물어봄
public class GoogleOAuth2Client implements OAuth2Client {

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public GoogleOAuth2Client(
            RestClient.Builder restClientBuilder,
            @Value("${oauth2.google.client-id}") String clientId,
            @Value("${oauth2.google.client-secret}") String clientSecret,
            @Value("${oauth2.google.redirect-uri}") String redirectUri) {
        this.restClient = restClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    @Override
    public Provider provider() {
        return Provider.GOOGLE;
    }

    @Override
    public OAuth2UserInfo fetchUserInfo(String code) {
        String accessToken = exchangeCodeForAccessToken(code); // 1. code -> access token
        GoogleUserInfoResponse userInfo = fetchGoogleUserInfo(accessToken); // 2. access token -> 유저 정보

        return new OAuth2UserInfo(Provider.GOOGLE, userInfo.sub(), userInfo.email(), userInfo.name());
    }

    private String exchangeCodeForAccessToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");
        // form에 담긴 건 구글이 요구하는 양식 ("나 이 앱이고 , 이 code를 받았고 , 여기로 다시 알려줘")

        GoogleTokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URI) // 이 주소로 POST 요청을 보냄
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form) // 그 양식을 요청 본문에 넣음
                .retrieve() // "응답 와라 , 받으면 GoogleTokenResponse(JSON의 access_token 필드) 자동으로 변환해준다
                .body(GoogleTokenResponse.class);

        // // 구글이 {"access_token": "ya29.xxxxx"} 같은 JSON을 주면 → tokenResponse.accessToken()으로 꺼냄
        return tokenResponse.accessToken();
    }

    private GoogleUserInfoResponse fetchGoogleUserInfo(String accessToken) {
        return restClient.get()
                .uri(USERINFO_URI)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleUserInfoResponse.class);

        // 이번엔 GET 요청 , 헤더에 "Bearer ya29.xxxxx"(아까 받은 출입증)을 넣어서 보냄
        // 구글이 {"sub": "구글고유ID", "email": "a@b.com", "name": "홍길동"} 돌려줌 → GoogleUserInfoResponse 객체로 변환됨
    }
}
