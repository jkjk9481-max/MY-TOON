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

class GoogleOAuth2ClientTest {

    @Test
    @DisplayName("code로 토큰을 교환하고, 그 토큰으로 구글 사용자 정보를 받아온다")
    void fetchUserInfo_exchangesCodeForToken_thenFetchesUserInfo() {
        // given: 구글 서버 2번 호출(토큰 교환 -> 사용자 정보 조회)을 가짜 응답으로 흉내
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"access_token":"google-access-token"}
                        """, MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://www.googleapis.com/oauth2/v3/userinfo"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"sub":"google-123","email":"a@b.com","name":"홍길동"}
                        """, MediaType.APPLICATION_JSON));

        GoogleOAuth2Client client = new GoogleOAuth2Client(
                builder, "client-id", "client-secret", "https://myapp.example/callback");

        // when
        OAuth2UserInfo userInfo = client.fetchUserInfo("auth-code");

        // then
        assertThat(userInfo.provider()).isEqualTo(Provider.GOOGLE);
        assertThat(userInfo.providerUserId()).isEqualTo("google-123");
        assertThat(userInfo.email()).isEqualTo("a@b.com");
        assertThat(userInfo.nickname()).isEqualTo("홍길동");

        server.verify(); // 기대했던 두 호출이 실제로 일어났는지 확인
    }
}
