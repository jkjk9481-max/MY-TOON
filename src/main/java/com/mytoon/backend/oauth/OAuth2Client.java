package com.mytoon.backend.oauth;

import com.mytoon.backend.user.entity.Provider;

// 구글/카카오 등 소셜 프로바이더 하나에 대응하는 구현체가 이 인터페이스를 구현한다.
// "코드 주면 유저정보 갖고 와" 라는 업무 명세서
public interface OAuth2Client {

    Provider provider();

    // code(인가 코드)를 받아 "code -> 토큰 교환 -> 사용자 정보 조회"까지 끝낸 결과를 돌려준다.
    OAuth2UserInfo fetchUserInfo(String code);
}
