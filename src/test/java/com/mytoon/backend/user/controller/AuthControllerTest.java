package com.mytoon.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mytoon.backend.oauth.OAuth2Client;
import com.mytoon.backend.oauth.OAuth2ClientRegistry;
import com.mytoon.backend.oauth.OAuth2UserInfo;
import com.mytoon.backend.security.JwtAuthenticationFilter;
import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.entity.User;
import com.mytoon.backend.user.service.AuthService;
import com.mytoon.backend.user.service.LoginResult;

@WebMvcTest(AuthController.class) // 웹 계층(컨트롤러 , JSON 변환 , 검증)만 띄운다. Service/Repository/DB는 안 뜬다.
@AutoConfigureMockMvc(addFilters = false) // SecurityConfig는 떠있어도, 이 테스트는 컨트롤러 동작만 보고 싶어서 보안 필터는 끈다
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // 실제 서버 없이 HTTP 요청을 흉내 내 쏘는 도구

    @MockitoBean
    private OAuth2ClientRegistry oAuth2ClientRegistry; // code -> 유저정보 교환 과정은 통째로 가짜로 대체

    @MockitoBean // 스프링 컨텍스트 안의 AuthService를 '가짜'로 갈아끼운다 (DB까지 안 내려감)
    private AuthService authService;

    @MockitoBean // SecurityConfig가 빈을 만들 때 필요해서 끼워주는 것뿐, addFilters=false라 실제로 동작은 안 함
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("올바른 로그인 요청이면 200과 함께 로그인 결과 JSON을 돌려준다")
    void login_validRequest_returnsLoginResponse() throws Exception {
        // given: OAuth2Client가 code를 받아 유저정보를 돌려준다고 흉내
        OAuth2Client googleClient = mock(OAuth2Client.class);
        when(oAuth2ClientRegistry.get(Provider.GOOGLE)).thenReturn(googleClient);
        when(googleClient.fetchUserInfo("auth-code"))
                .thenReturn(new OAuth2UserInfo(Provider.GOOGLE, "google-123", "a@b.com", "홍길동"));

        // AuthService가 그 유저정보로 로그인/가입을 처리한다고 흉내 (DB 없이 User는 mock으로)
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getNickname()).thenReturn("홍길동");
        when(mockUser.getRole()).thenReturn(Role.USER);
        when(authService.loginOrRegister(eq(Provider.GOOGLE), eq("google-123"), eq("a@b.com"), eq("홍길동")))
                .thenReturn(new LoginResult(mockUser, "fake-access-token", "raw-refresh-token"));

        String body = """
                {"code":"auth-code"}
                """;

        // when & then: 요청을 쏘고 응답 상태/JSON 필드를 검증
        mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nickname").value("홍길동"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.accessToken").value("fake-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("raw-refresh-token"));

        // 이 테스트가 증명하는 것
        //  1. 경로의 provider("google")가 Provider.GOOGLE로 바르게 해석된다.
        //  2. 컨트롤러가 OAuth2ClientRegistry에서 받은 유저정보를 AuthService에 그대로 전달한다.
        //  3. 서비스 결과를 LoginResponse로 바꿔 JSON으로 내려준다. (LoginResponse.from)
    }

    @Test
    @DisplayName("code가 비어 있으면 400을 반환하고 서비스는 호출되지 않는다")
    void login_blankCode_returnsBadRequest() throws Exception {
        String body = """
                {"code":""}
                """;

        mockMvc.perform(post("/api/auth/google/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest()); // 응답이 400인가?

        // 검증에서 막혔으므로 OAuth2 교환도, 서비스도 아예 호출되지 않아야 한다
        verify(oAuth2ClientRegistry, never()).get(any());
        verify(authService, never()).loginOrRegister(any(), any(), any(), any());
    }
}
