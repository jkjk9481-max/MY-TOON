package com.mytoon.backend.webtoon.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mytoon.backend.webtoon.controller.dto.WebtoonCreateRequest;
import com.mytoon.backend.webtoon.controller.dto.WebtoonUpdateRequest;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;
    private final EpisodeRepository episodeRepository;

    @Transactional
    public Webtoon create(WebtoonCreateRequest request) {
        String title = requireNonBlank(request.title(), "title");

        if (webtoonRepository.existsByTitle(title)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 작품 제목입니다.");
        }

        return webtoonRepository.save(
                Webtoon.builder()
                        .title(title)
                        .description(request.description())
                        .authorName(requireNonBlank(request.authorName(), "authorName"))
                        .build()
                
                // 흐름 제목 다듬기 -> 중복 확인 -> 진짜로 DB에 저장
        );
    }

    @Transactional
    public Webtoon update(Long id, WebtoonUpdateRequest request) {
        Webtoon webtoon = findOrThrow(id);

        if (request.title() != null) {
            String title = requireNonBlank(request.title(), "title");
            if (webtoonRepository.existsByTitleAndIdNot(title, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 작품 제목입니다.");
            }
            webtoon.updateTitle(title);
        }

        if (request.description() != null) {
            webtoon.updateDescription(request.description());
        }

        if (request.authorName() != null) {
            webtoon.updateAuthorName(requireNonBlank(request.authorName(), "authorName"));
        }

        return webtoon;
    }

    @Transactional
    public void delete(Long id) {
        Webtoon webtoon = findOrThrow(id);

        if (episodeRepository.existsByWebtoonId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "회차가 남아있는 작품은 삭제할 수 없습니다.");
        }

        webtoonRepository.delete(webtoon);
    }

    private Webtoon findOrThrow(Long id) {
        return webtoonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작품입니다."));
    }

    private String requireNonBlank(String value, String fieldName) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "은(는) 비어있을 수 없습니다.");
        }
        return trimmed;
        // 흐름 id로 작품찾기(없으면 404) -> 그 작품에 회차가 하나라도 있는지 확인 -> 있으면 409 거절 -> 없으면 그제서야 진짜 삭제
    }
}
