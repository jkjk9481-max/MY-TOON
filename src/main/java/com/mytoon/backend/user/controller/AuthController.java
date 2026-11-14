package com.mytoon.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.oauth.OAuth2ClientRegistry;
import com.mytoon.backend.oauth.OAuth2UserInfo;
import com.mytoon.backend.user.controller.dto.LoginResponse;
import com.mytoon.backend.user.controller.dto.OAuthLoginRequest;
import com.mytoon.backend.user.controller.dto.RefreshTokenRequest;
import com.mytoon.backend.user.entity.Provider;
import com.mytoon.backend.user.service.AuthService;
import com.mytoon.backend.user.service.LoginResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2ClientRegistry oAuth2ClientRegistry;
    private final AuthService authService;

    @PostMapping("/{provider}/login")
    public LoginResponse login(@PathVariable String provider, @Valid @RequestBody OAuthLoginRequest request) {
        Provider parsedProvider = Provider.valueOf(provider.toUpperCase());
        OAuth2UserInfo userInfo = oAuth2ClientRegistry.get(parsedProvider).fetchUserInfo(request.code());
        // "parsedProvider 값으로 구글/카카오 중 담당 객체를 찾고 → 그 객체에게 code를 줘서 사용자 정보를 받아오게 시키고
        // -> 받아온 사용자 정보를 userInfo 변수에 저장한다

        // "구글 담당 통역사 주세요" , 그 통역사한테 "이 코드로 유저정보 가져와" 시킴
        // 여기서 컨트롤러는 구글 통역사가 정확히 뭘 하는지 전혀 모른다

        LoginResult result = authService.loginOrRegister(
                userInfo.provider(),
                userInfo.providerUserId(),
                userInfo.email(),
                userInfo.nickname()
        );
        return LoginResponse.from(result);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResult result = authService.refresh(request.refreshToken());
        return LoginResponse.from(result);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
    }
}
