package com.mytoon.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Role;

// /api/admin/** 에 대한 권한 규칙만 검증한다. 아직 관리자 컨트롤러가 없으므로 존재하지 않는 경로를 쓴다:
// 보안 필터를 "통과"하면 404(없는 경로), 막히면 401/403이 나오므로 상태 코드로 구분할 수 있다.
@SpringBootTest
@AutoConfigureMockMvc
class AdminAccessIntegrationTest {

    private static final String ADMIN_PATH = "/api/admin/security-check";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰이 없으면 /api/admin/** 은 401")
    void admin_noToken_returns401() throws Exception {
        mockMvc.perform(get(ADMIN_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER 토큰이면 /api/admin/** 은 403")
    void admin_userToken_returns403() throws Exception {
        String userToken = jwtTokenProvider.generateAccessToken(1L, Role.USER);

        mockMvc.perform(get(ADMIN_PATH).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN 토큰이면 보안 필터를 통과한다 (아직 컨트롤러가 없어 404)")
    void admin_adminToken_passesSecurity() throws Exception {
        String adminToken = jwtTokenProvider.generateAccessToken(1L, Role.ADMIN);

        mockMvc.perform(get(ADMIN_PATH).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자가 아닌 경로(/api/admin 밖)는 USER 토큰으로도 보안 필터를 통과한다")
    void nonAdminPath_userToken_passesSecurity() throws Exception {
        String userToken = jwtTokenProvider.generateAccessToken(1L, Role.USER);

        mockMvc.perform(get("/api/webtoons").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
