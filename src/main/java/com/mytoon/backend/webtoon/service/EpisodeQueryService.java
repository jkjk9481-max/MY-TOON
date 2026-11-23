package com.mytoon.backend.webtoon.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.webtoon.controller.dto.EpisodeAccessResponse;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

// 독자(비회원/회원)용 회차 조회. 관리자용 쓰기 로직(EpisodeService)과는 책임이 달라 분리함.
@Service
@RequiredArgsConstructor
public class EpisodeQueryService {

    private final EpisodeRepository episodeRepository;
    private final WebtoonRepository webtoonRepository;

    public List<EpisodeAccessResponse> list(Long webtoonId, boolean isLoggedIn) {
        if (!webtoonRepository.existsById(webtoonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작품입니다.");
        }

        return episodeRepository.findByWebtoonIdOrderByEpisodeNumberAsc(webtoonId).stream()
                .filter(episode -> isVisible(episode, isLoggedIn))
                .map(episode -> toResponse(episode, isLoggedIn))
                .toList();
    }

    // DRAFT는 항상 숨김, PREVIEW는 로그인한 회원에게만 보임(잠긴 채로), PUBLIC은 누구에게나 보임
    private boolean isVisible(Episode episode, boolean isLoggedIn) {
        if (episode.getStatus() == EpisodeStatus.DRAFT) {
            return false;
        }
        if (episode.getStatus() == EpisodeStatus.PREVIEW) {
            return isLoggedIn;
        }
        return true;
    }

    private EpisodeAccessResponse toResponse(Episode episode, boolean isLoggedIn) {
        if (episode.getStatus() == EpisodeStatus.PREVIEW) {
            return new EpisodeAccessResponse(episode.getEpisodeNumber(), episode.getTitle(), false, "INK_REQUIRED");
        }

        boolean accessible = episode.getEpisodeNumber() == 1 || isLoggedIn;
        String reason = accessible ? null : "LOGIN_REQUIRED";
        return new EpisodeAccessResponse(episode.getEpisodeNumber(), episode.getTitle(), accessible, reason);
    }
}
