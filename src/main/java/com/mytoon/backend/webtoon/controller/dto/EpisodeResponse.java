package com.mytoon.backend.webtoon.controller.dto;

import java.time.Instant;

import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;

public record EpisodeResponse(
        Long id,
        Long webtoonId,
        int episodeNumber,
        String title,
        EpisodeStatus status,
        Instant createdAt
) {
    public static EpisodeResponse from(Episode episode) {
        return new EpisodeResponse(
                episode.getId(),
                episode.getWebtoon().getId(),
                episode.getEpisodeNumber(),
                episode.getTitle(),
                episode.getStatus(),
                episode.getCreatedAt()
        );
    }
}
