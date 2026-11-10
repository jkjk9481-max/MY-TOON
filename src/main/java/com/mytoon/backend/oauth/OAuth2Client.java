package com.mytoon.backend.oauth;

import com.mytoon.backend.user.entity.Provider;

// 구글/카카오 등 소셜 프로바이더 하나에 대응하는 구현체가 이 인터페이스를 구현한다.
// "코드 주면 유저정보 갖고 와" 라는 업무 명세서

// interface는 "이런 메서드들이 있어야한다"는 약속만 정의하고 , 실제 구현은 없다
// 여기선 구글이든 카카오든 , 소셜 로그인 담당자라면 반드시 provider(내가 누구 담당인지)와 fetchUserInfo(code)(인가 코드 하나 받으면 사용자 정보 하나 돌려주기)를 할수있어야한다
public interface OAuth2Client {
// "무엇을 할지"만 정의하는 명세서
// 실제 구현은 클래스가함
// 그래서 새 프로바이더(애플 등)를 추가해도 기존 코드(Controller, Registry)는 안 건드려도됨

    Provider provider();

    // code(인가 코드)를 받아 "code -> 토큰 교환 -> 사용자 정보 조회"까지 끝낸 결과를 돌려준다.
    OAuth2UserInfo fetchUserInfo(String code);
}
