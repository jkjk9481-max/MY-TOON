package com.mytoon.backend.user.service;

import com.mytoon.backend.user.entity.User;

// 로그인 처리를 끝낸 결과를 돌려줌 ( 서버 내부용 , 엔티티포함)
public record LoginResult(User user, String accessToken, String refreshToken) {
}
