package com.mytoon.backend.webtoon.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.webtoon.controller.dto.EpisodeCreateRequest;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final WebtoonRepository webtoonRepository;

    @Transactional
    public Episode create(Long webtoonId, EpisodeCreateRequest request) {
        Webtoon webtoon = webtoonRepository.findById(webtoonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작품입니다."));

        String title = requireNonBlank(request.title(), "title");

        int expectedNumber = episodeRepository.findMaxEpisodeNumberByWebtoonId(webtoonId) + 1;
        if (request.episodeNumber() != expectedNumber) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "다음 회차 번호는 " + expectedNumber + "입니다.");
        }

        if (episodeRepository.existsByWebtoonIdAndTitle(webtoonId, title)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "같은 작품에 이미 존재하는 회차 제목입니다.");
        }

        return episodeRepository.save(
                Episode.builder()
                        .webtoon(webtoon)
                        .episodeNumber(request.episodeNumber())
                        .title(title)
                        .status(EpisodeStatus.DRAFT) //새 회차는 항상 DRAFT로 시작
                        .build()
        );
    }

    private String requireNonBlank(String value, String fieldName) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "은(는) 비어있을 수 없습니다.");
        }
        return trimmed;
    }

    @Transactional
    public Episode updateTitle(Long webtoonId, Long episodeId, String newTitle) {

        Episode episode = episodeRepository.findByIdAndWebtoonId(episodeId, webtoonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND , "존재하지 않는 회차입니다."));


        String title = requireNonBlank(newTitle, "newTitle");

        if(episodeRepository.existsByWebtoonIdAndTitleAndIdNot(episode.getWebtoon().getId(), title, episodeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT , "같은 작품에 이미 존재하는 회차 제목입니다.");
        }

        episode.updateTitle(title);

        return episodeRepository.save(episode);
    }

    @Transactional
    public Episode updateStatus(Long webtoonId, Long episodeId, EpisodeStatus newStatus) {
        Episode episode = episodeRepository.findByIdAndWebtoonId(episodeId, webtoonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."));

        if (!episode.getStatus().canMoveTo(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    episode.getStatus() + "에서 " + newStatus + "(으)로는 변경할 수 없습니다.");
        }

        episode.updateStatus(newStatus);
        return episodeRepository.save(episode);
    }
}
