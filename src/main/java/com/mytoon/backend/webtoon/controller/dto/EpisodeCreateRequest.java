package com.mytoon.backend.webtoon.controller.dto;

import jakarta.validation.constraints.NotBlank;

// record는 getter, 생성자 , equals / hashCode를 자바가 자동으로 만들어줌
public record EpisodeCreateRequest(
        int episodeNumber,
        @NotBlank String title // 제목이 비어있으면 안됨
) {
}
