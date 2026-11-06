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

import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.user.entity.User;
import com.mytoon.backend.user.service.AuthService;
import com.mytoon.backend.user.service.LoginResult;

@WebMvcTest(AuthController.class) // 웹 계층(컨트롤러 , JSON 변환 , 검증)만 띄운다. Service/Repository/DB는 안 뜬다.
@AutoConfigureMockMvc(addFilters = false) // SecurityConfig가 아직 없어서, 보안 필터는 끄고 컨트롤러 동작만 본다
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // 실제 서버 없이 HTTP 요청을 흉내 내 쏘는 도구

    @MockitoBean // 스프링 컨텍스트 안의 AuthService를 '가짜'로 갈아끼운다 (DB까지 안 내려감)
    private AuthService authService;

    @Test
    @DisplayName("올바른 로그인 요청이면 200과 함께 로그인 결과 JSON을 돌려준다")
    void login_validRequest_returnsLoginResponse() throws Exception {
        // given: 서비스가 어떤 User+토큰을 돌려준다고 흉내 (DB 없이 User는 mock으로)
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getNickname()).thenReturn("홍길동");
        when(mockUser.getRole()).thenReturn(Role.USER);
        when(authService.loginOrRegister(eq(Provider.GOOGLE), eq("google-123"), eq("a@b.com"), eq("홍길동")))
                .thenReturn(new LoginResult(mockUser, "raw-refresh-token"));

        String body = """
                {"provider":"GOOGLE","providerUserId":"google-123","email":"a@b.com","nickname":"홍길동"}
                """;

        // when & then: 요청을 쏘고 응답 상태/JSON 필드를 검증
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nickname").value("홍길동"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.refreshToken").value("raw-refresh-token"));

        // 이 테스트가 증명하는 것
        //  1. 컨트롤러가 JSON을 LoginRequest로 잘 변환합니다. (@RequestBody)
        //  2. 그 값을 서비스에 순서대로 잘 넘깁니다.
        //  3. 서비스 결과를 LoginResponse로 바꿔 JSON으로 내려줍니다. (LoginResponse.from)
    }

    @Test
    @DisplayName("providerUserId가 비어 있으면 400을 반환하고 서비스는 호출되지 않는다")
    void login_blankProviderUserId_returnsBadRequest() throws Exception {
        // providerUserId가 빈 문자열 -> @NotBlank 위반
        String body = """
                {"provider":"GOOGLE","providerUserId":"","email":"a@b.com","nickname":"홍길동"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest()); // 응답이 400인가?

        // 검증에서 막혔으므로 서비스는 아예 호출되지 않아야 한다
        verify(authService, never()).loginOrRegister(any(), any(), any(), any()); // 서비스가 안 불렸나

        //  빈 값으로 요청을 보냈을 때:
        //  1. 응답이 400인가? (잘못된 요청이라고 거절했나)
        //  2. 서비스는 호출 안 됐나? (거절이 서비스에 가기 전에 이루어졌나)
        //  -> 둘다 맞으면 통과
    }
}
