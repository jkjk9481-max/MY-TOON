package com.mytoon.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
//우체부를(RestClient.Builder)를 구글 / 카카오 통역사가 각자 따로 받게 함 - 같은 우체부를 공유하면 나중에 한쪽 설정이 다른쪽으로 세어나갈수있어서
public class RestClientConfig {

    // 빌더는 상태를 가질 수 있어 공유하면 위험하므로, 주입받는 쪽마다 새 인스턴스를 받게 prototype으로 둔다.
    @Bean
    @Scope("prototype")
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
