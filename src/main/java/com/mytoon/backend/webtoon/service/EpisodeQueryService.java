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
        if (!webtoonRepository.existsById(webtoonId)) { // 요청한 작품 번호가 DB에 있는지 확인한다 없으면 404 던지고 에외
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작품입니다.");
        }

        // 그 작품의 회차를 전부(DRAFT / PREVIEW / PUBLIC 가릴 것 없이) 번호 순서로 DB에서 가져온다
        return episodeRepository.findByWebtoonIdOrderByEpisodeNumberAsc(webtoonId).stream()
                .filter(episode -> isVisible(episode, isLoggedIn)) // 가져온 회차들 중에서 "이 사용자에게 보여줘도 되는것"만 걸러낸다
                .map(episode -> toResponse(episode, isLoggedIn)) // 걸러진 회차들을 DTO로 변환
                .toList(); // 리스트로 모아서 돌려줌
    }

    // DRAFT는 항상 숨김, PREVIEW는 로그인한 회원에게만 보임(잠긴 채로), PUBLIC은 누구에게나 보임
    private boolean isVisible(Episode episode, boolean isLoggedIn) {
        if (episode.getStatus() == EpisodeStatus.DRAFT) {
            return false; // 안보여줌
        }
        if (episode.getStatus() == EpisodeStatus.PREVIEW) {
            return isLoggedIn; // 로그인했으면 true , 비회원이면 false
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
