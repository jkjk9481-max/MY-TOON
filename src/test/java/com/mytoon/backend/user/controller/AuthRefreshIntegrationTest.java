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
}
