package com.mytoon.backend.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mytoon.backend.user.entity.Provider;

// Provider(GOOGLE/KAKAO)에 맞는 OAuth2Client 구현체를 찾아주는 창구.
// 새 프로바이더가 추가되면 OAuth2Client 구현체만 추가하면 되고, 이 클래스는 손댈 필요 없다.
@Component
// 컨트롤러가 "구글 통역사 주세요"하면 찾아서 건네줌 . 애플 로그인이 추가돼도 여기 코드는 안바뀜
public class OAuth2ClientRegistry {


    // - @Component: new로 직접 안 만들어도, Spring이 미리 만들어서 보관하고 있다가 필요한 곳에 꺼내줌
    //- Map: GOOGLE이라는 키로 찾으면, 그에 대응하는 GoogleOAuth2Client 객체(값)을 바로 꺼낼 수 있음
    private final Map<Provider, OAuth2Client> clientsByProvider;

    public OAuth2ClientRegistry(List<OAuth2Client> clients) {
        this.clientsByProvider = clients.stream()
                .collect(Collectors.toMap(OAuth2Client::provider, Function.identity()));
    }

    public OAuth2Client get(Provider provider) {
        OAuth2Client client = clientsByProvider.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        }
        return client;
    }
}
