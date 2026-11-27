package com.mytoon.backend.webtoon.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.webtoon.controller.dto.EpisodeImageOrderResponse;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeImage;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.repository.EpisodeImageRepository;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 독자(비회원/회원)용 회차 이미지 조회. 목록과 파일 모두 같은 권한 검사를 거친다.
@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeImageQueryService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeImageRepository episodeImageRepository;
    private final EpisodeImageStorage episodeImageStorage;

    public List<EpisodeImageOrderResponse> list(Long webtoonId, Long episodeId, boolean isLoggedIn) {
        checkReadable(webtoonId, episodeId, isLoggedIn);

        return episodeImageRepository.findByEpisodeIdOrderByOrderAsc(episodeId).stream()
                .map(image -> new EpisodeImageOrderResponse(image.getOrder()))
                .toList();
    }

    public Resource load(Long webtoonId, Long episodeId, int order, boolean isLoggedIn) {
        checkReadable(webtoonId, episodeId, isLoggedIn); // 이미지를 찾기 전에 권한부터 확인

        EpisodeImage image = episodeImageRepository.findByEpisodeIdAndOrder(episodeId, order)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 이미지입니다."));

        Path file = episodeImageStorage.resolve(image.getPath());
        if (!Files.isRegularFile(file)) { // DB 기록은 있는데 파일이 없는 상태 -> 개발자가 알 수 있게 로그를 남김
            log.error("회차 이미지 파일이 없습니다. episodeId={}, order={}, path={}", episodeId, order, image.getPath());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 이미지입니다.");
        }
        return new FileSystemResource(file);
    }

    // 검사 순서가 중요하다: 존재 여부(404) -> PUBLIC 1화 허용 -> 비회원(401) -> PREVIEW(403)
    private void checkReadable(Long webtoonId, Long episodeId, boolean isLoggedIn) {
        Episode episode = episodeRepository.findByIdAndWebtoonId(episodeId, webtoonId)
                .filter(found -> found.getStatus() != EpisodeStatus.DRAFT) // DRAFT는 존재 자체를 숨김
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."));

        if (episode.getStatus() == EpisodeStatus.PUBLIC && episode.getEpisodeNumber() == 1) {
            return;
        }
        if (!isLoggedIn) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (episode.getStatus() == EpisodeStatus.PREVIEW) { // 잉크 구매는 Phase 2: 지금은 구매한 사람이 없으므로 모두 차단
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "구매가 필요한 회차입니다.");
        }
    }
}
