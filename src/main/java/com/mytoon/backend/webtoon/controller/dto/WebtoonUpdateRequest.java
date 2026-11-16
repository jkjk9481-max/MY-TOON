package com.mytoon.backend.webtoon.controller.dto;

// PATCH: 보낸 필드만 수정. null인 필드는 "변경 안 함"을 의미 (값을 지우는 기능은 아직 없음).
public record WebtoonUpdateRequest(
        String title,
        String description,
        String authorName
) {
}
