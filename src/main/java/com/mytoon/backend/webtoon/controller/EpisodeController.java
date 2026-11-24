package com.mytoon.backend.webtoon.controller;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.webtoon.controller.dto.EpisodeAccessResponse;
import com.mytoon.backend.webtoon.service.EpisodeQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webtoons/{webtoonId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeQueryService episodeQueryService;

    @GetMapping
    // Authentication == "이 요청을 보낸 사람이 누구인지"대한 정보 객체 
    // 로그인한 회원이 보낸 요청이면 그 회원 정보가 담긴 객체 , 로그인 안 한 사람이 보낸 요청이면 비회원을 뜻하는 객체가 들어온다
    public List<EpisodeAccessResponse> list(@PathVariable Long webtoonId, Authentication authentication) {
        boolean isLoggedIn = authentication != null // 요청 보낸 사람 정보 자체가있다
                && authentication.isAuthenticated() // 그 정보 객체가 "인증됨" 상태다
                && !(authentication instanceof AnonymousAuthenticationToken); // 그런데 그 "인증됨"이 비회원용 가짜 인증이 아니다
        return episodeQueryService.list(webtoonId, isLoggedIn);
    }
}
