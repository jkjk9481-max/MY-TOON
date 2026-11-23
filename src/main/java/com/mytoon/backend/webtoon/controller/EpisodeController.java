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
    public List<EpisodeAccessResponse> list(@PathVariable Long webtoonId, Authentication authentication) {
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        return episodeQueryService.list(webtoonId, isLoggedIn);
    }
}
