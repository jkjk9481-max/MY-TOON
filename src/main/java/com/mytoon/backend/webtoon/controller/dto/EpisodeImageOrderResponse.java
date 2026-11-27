package com.mytoon.backend.webtoon.controller.dto;

// 독자용 이미지 목록 응답. 서버의 저장 경로(path)는 노출하지 않고 컷 순서만 내려준다.
public record EpisodeImageOrderResponse(
        int order
) {
}
