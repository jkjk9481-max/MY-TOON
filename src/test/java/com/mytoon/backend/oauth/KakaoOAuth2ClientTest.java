package com.mytoon.backend.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.mytoon.backend.user.entity.Provider;

class KakaoOAuth2ClientTest {

    @Test
    @DisplayName("code로 토큰을 교환하고, 그 토큰으로 카카오 사용자 정보를 받아온다")
    void fetchUserInfo_exchangesCodeForToken_thenFetchesUserInfo() {
        // given: 카카오 서버 2번 호출(토큰 교환 -> 사용자 정보 조회)을 가짜 응답으로 흉내
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"access_token":"kakao-access-token"}
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "id": 999,
                          "kakao_account": {
                            "email": "a@b.com",
                            "profile": { "nickname": "홍길동" }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        KakaoOAuth2Client client = new KakaoOAuth2Client(
                builder, "client-id", "client-secret", "https://myapp.example/callback");

        // when
        OAuth2UserInfo userInfo = client.fetchUserInfo("auth-code");

        // then
        assertThat(userInfo.provider()).isEqualTo(Provider.KAKAO);
        assertThat(userInfo.providerUserId()).isEqualTo("999");
        assertThat(userInfo.email()).isEqualTo("a@b.com");
        assertThat(userInfo.nickname()).isEqualTo("홍길동");

        server.verify();
    }
}
