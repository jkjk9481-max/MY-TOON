package com.mytoon.backend.webtoon.controller.dto;

import java.time.Instant;

import com.mytoon.backend.webtoon.entity.Webtoon;

public record WebtoonResponse(
        Long id,
        String title,
        String description,
        String authorName,
        Instant createdAt
) {
    public static WebtoonResponse from(Webtoon webtoon) {
        return new WebtoonResponse(
                webtoon.getId(),
                webtoon.getTitle(),
                webtoon.getDescription(),
                webtoon.getAuthorName(),
                webtoon.getCreatedAt()
        );
    }
}
