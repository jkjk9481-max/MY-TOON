package com.mytoon.backend.webtoon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mytoon.backend.webtoon.controller.dto.WebtoonCreateRequest;
import com.mytoon.backend.webtoon.controller.dto.WebtoonResponse;
import com.mytoon.backend.webtoon.controller.dto.WebtoonUpdateRequest;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.service.WebtoonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/webtoons")
@RequiredArgsConstructor
public class AdminWebtoonController {
    // 요청을 받아서 -> Service에 그대로 넘기고 -> Service가 돌려준 엔티티를 DTO로 포장해서 반환 ( 비즈니스 규칙 )은 전혀 모름

    private final WebtoonService webtoonService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebtoonResponse create(@Valid @RequestBody WebtoonCreateRequest request) {
        Webtoon webtoon = webtoonService.create(request);
        return WebtoonResponse.from(webtoon);
    }

    @PatchMapping("/{id}")
    public WebtoonResponse update(@PathVariable Long id, @RequestBody WebtoonUpdateRequest request) {
        Webtoon webtoon = webtoonService.update(id, request);
        return WebtoonResponse.from(webtoon);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        webtoonService.delete(id);
    }
}
