package com.mytoon.backend.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.user.controller.dto.LoginRequest;
import com.mytoon.backend.user.controller.dto.LoginResponse;
import com.mytoon.backend.user.service.AuthService;
import com.mytoon.backend.user.service.LoginResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// TODO(임시): OAuth2 연동 전까지만 쓰는 dev 전용 로그인 엔드포인트.
//  실제 로그인 입구는 OAuth2 콜백이 되므로, OAuth2를 붙이면 이 컨트롤러는 제거/교체한다.
//  지금 목적은 AuthService.loginOrRegister가 HTTP 레벨에서 제대로 도는지 확인하는 것뿐.
@RestController // HTTP 요청을 처리하는 컨트롤러이고 , 리턴값을 JSON으로 바꿔서 응답 바디에 바로 넣는 어노테이션
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // @RequestBody = JSON을 Request객체로 변환해서 넣어줘
        LoginResult result = authService.loginOrRegister(
                request.provider(),
                request.providerUserId(),
                request.email(),
                request.nickname()
        );
        return LoginResponse.from(result);
    }
}
