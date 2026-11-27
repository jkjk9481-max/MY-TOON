package com.mytoon.backend.webtoon.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.webtoon.controller.dto.EpisodeAccessResponse;
import com.mytoon.backend.webtoon.controller.dto.EpisodeImageOrderResponse;
import com.mytoon.backend.webtoon.service.EpisodeImageQueryService;
import com.mytoon.backend.webtoon.service.EpisodeQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webtoons/{webtoonId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeQueryService episodeQueryService;
    private final EpisodeImageQueryService episodeImageQueryService;

    @GetMapping
    // Authentication == "이 요청을 보낸 사람이 누구인지"대한 정보 객체 
    // 로그인한 회원이 보낸 요청이면 그 회원 정보가 담긴 객체 , 로그인 안 한 사람이 보낸 요청이면 비회원을 뜻하는 객체가 들어온다
    public List<EpisodeAccessResponse> list(@PathVariable Long webtoonId, Authentication authentication) {
        return episodeQueryService.list(webtoonId, isLoggedIn(authentication));
    }

    @GetMapping("/{episodeId}/images")
    public List<EpisodeImageOrderResponse> images(
            @PathVariable Long webtoonId, @PathVariable Long episodeId, Authentication authentication) {
        return episodeImageQueryService.list(webtoonId, episodeId, isLoggedIn(authentication));
    }

    @GetMapping("/{episodeId}/images/{order}")
    public ResponseEntity<Resource> image(
            @PathVariable Long webtoonId, @PathVariable Long episodeId, @PathVariable int order,
            Authentication authentication) {
        Resource resource = episodeImageQueryService.load(webtoonId, episodeId, order, isLoggedIn(authentication));
        MediaType contentType = MediaTypeFactory.getMediaType(resource) // 파일 확장자로 Content-Type 결정
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(contentType).body(resource);
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null // 요청 보낸 사람 정보 자체가있다
                && authentication.isAuthenticated() // 그 정보 객체가 "인증됨" 상태다
                && !(authentication instanceof AnonymousAuthenticationToken); // 그런데 그 "인증됨"이 비회원용 가짜 인증이 아니다
    }
}
