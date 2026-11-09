package com.mytoon.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// TODO(임시): JWT 인증 필터가 들어가기 전까지의 최소 설정.
//  목적은 딱 하나 - 로그인 엔드포인트(/api/auth/**)를 Spring Security 기본 설정(모든 요청 인증 요구)으로부터 풀어주는 것.
//  JWT 필터를 붙이는 단계에서 이 클래스는 통째로 다시 설계한다.
@Configuration
// 지금은 /api/auth/**만 통과시켜줌
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 세션 쿠키를 쓰지 않는 stateless REST API라 CSRF 방어 대상이 없음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
