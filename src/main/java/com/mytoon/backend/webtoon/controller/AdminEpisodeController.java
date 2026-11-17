package com.mytoon.backend.webtoon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.webtoon.controller.dto.EpisodeCreateRequest;
import com.mytoon.backend.webtoon.controller.dto.EpisodeResponse;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.service.EpisodeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/webtoons/{webtoonId}/episodes")
@RequiredArgsConstructor
public class AdminEpisodeController {

    private final EpisodeService episodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpisodeResponse create(@PathVariable Long webtoonId, @Valid @RequestBody EpisodeCreateRequest request) {
        Episode episode = episodeService.create(webtoonId, request);
        return EpisodeResponse.from(episode);
    }
}
