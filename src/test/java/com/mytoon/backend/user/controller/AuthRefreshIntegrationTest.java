package com.mytoon.backend.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;
import com.mytoon.backend.oauth.OAuth2Client;
import com.mytoon.backend.oauth.OAuth2ClientRegistry;
import com.mytoon.backend.oauth.OAuth2UserInfo;
import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.repository.UserRepository;

// 구글/카카오로 나가는 외부 HTTP 호출만 가짜로 대체하고, 그 안쪽(Controller->Service->Repository->실제 Postgres)은
// 전부 진짜로 돌려서 "로그인으로 받은 진짜 Refresh Token이 실제 DB에서 rotation/탈취탐지 되는지" 확인한다.
@SpringBootTest
@AutoConfigureMockMvc
class AuthRefreshIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private OAuth2ClientRegistry oAuth2ClientRegistry;

    private Long createdUserId;

    @AfterEach
    void cleanUp() {
        // users 삭제 시 social_accounts/refresh_tokens는 ON DELETE CASCADE로 같이 지워짐
        if (createdUserId != null) {
            userRepository.deleteById(createdUserId);
        }
    }

    @Test
    @DisplayName("실제 DB에서: 로그인으로 받은 Refresh Token은 1번만 쓸 수 있고(rotation), 재사용하면 전체 기기가 무효화된다")
    void refresh_realToken_rotatesThenDetectsReuse() throws Exception {
        String providerUserId = "integration-test-" + UUID.randomUUID();

        OAuth2Client googleClient = mock(OAuth2Client.class);
        when(oAuth2ClientRegistry.get(Provider.GOOGLE)).thenReturn(googleClient);
        when(googleClient.fetchUserInfo("auth-code"))
                .thenReturn(new OAuth2UserInfo(Provider.GOOGLE, providerUserId, "it@test.com", "통합테스트유저"));

        // 1) 로그인 -> 진짜 Refresh Token 발급 (실제 DB에 저장됨)
        String loginJson = mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"auth-code\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        createdUserId = ((Number) JsonPath.read(loginJson, "$.userId")).longValue();
        String firstRefreshToken = JsonPath.read(loginJson, "$.refreshToken");
        assertThat(firstRefreshToken).isNotBlank();

        // 2) 그 진짜 토큰으로 refresh -> 200 + 새 토큰 (기존 토큰은 실제 DB에서 revoked 처리됨)
        String refreshJson = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + firstRefreshToken + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String secondRefreshToken = JsonPath.read(refreshJson, "$.refreshToken");
        assertThat(secondRefreshToken).isNotBlank().isNotEqualTo(firstRefreshToken);

        // 3) 이미 폐기된 첫 번째 토큰을 다시 사용 -> 탈취 의심으로 401 + 이 유저의 모든 토큰 무효화
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + firstRefreshToken + "\"}"))
                .andExpect(status().isUnauthorized());

        // 4) 방금 2번 단계에서 받은(아직 한 번도 안 쓴) 두 번째 토큰도 -> 3번의 전체 무효화 때문에 이제는 거부돼야 한다
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + secondRefreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("실제 DB에서: 한 기기만 로그아웃하면 그 토큰만 폐기되고, 다른 기기는 유지되며, 같은 요청을 반복해도 204다")
    void logout_realToken_revokesOnlyThatDevice() throws Exception {
        String providerUserId = "integration-test-" + UUID.randomUUID();

        OAuth2Client googleClient = mock(OAuth2Client.class);
        when(oAuth2ClientRegistry.get(Provider.GOOGLE)).thenReturn(googleClient);
        when(googleClient.fetchUserInfo("auth-code"))
                .thenReturn(new OAuth2UserInfo(Provider.GOOGLE, providerUserId, "it@test.com", "통합테스트유저"));

        // 같은 계정으로 두 번 로그인 = 두 기기(폰, PC)
        String phoneJson = mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"auth-code\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String pcJson = mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"auth-code\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        createdUserId = ((Number) JsonPath.read(phoneJson, "$.userId")).longValue();
        String phoneToken = JsonPath.read(phoneJson, "$.refreshToken");
        String pcToken = JsonPath.read(pcJson, "$.refreshToken");

        // 1) 폰에서 로그아웃 -> 204
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + phoneToken + "\"}"))
                .andExpect(status().isNoContent());

        // 2) 같은 요청을 또 보내도(연타/재시도) 에러 없이 204
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + phoneToken + "\"}"))
                .andExpect(status().isNoContent());

        // 3) 존재하지 않는 토큰도 204
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"no-such-token\"}"))
                .andExpect(status().isNoContent());

        // 4) 로그아웃 안 한 PC 토큰은 여전히 유효 -> refresh 성공
        //    (반드시 5번보다 먼저: 5번은 재사용 감지로 PC 토큰까지 전부 폐기시키기 때문)
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + pcToken + "\"}"))
                .andExpect(status().isOk());

        // 5) 로그아웃한 폰 토큰으로 refresh -> 거부 (폐기된 토큰 재사용이므로 기존 정책대로 401)
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + phoneToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }
}
