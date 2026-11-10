package com.mytoon.backend.oauth;

import com.mytoon.backend.user.entity.Provider;

// 심부름 다녀온 결과를 담은 봉투
// record --> 클래스들 만들려면 보통 필드 선언 + 생성자 + getter를 써야하는데
// record는 "이 4개 값을 가진 , 바뀌지않는 데이터 상자"라고 선언을하면 생성자랑 조회메서드를 자바가 자동으로 만들어준다
public record OAuth2UserInfo(
        Provider provider,
        String providerUserId,
        String email,
        String nickname
) {
}
