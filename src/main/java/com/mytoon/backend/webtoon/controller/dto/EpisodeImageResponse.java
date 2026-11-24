package com.mytoon.backend.webtoon.controller.dto;

import com.mytoon.backend.webtoon.entity.EpisodeImage;

public record EpisodeImageResponse(
        Long id,
        int order,
        String path
) {
    public static EpisodeImageResponse from(EpisodeImage image) {
        return new EpisodeImageResponse(image.getId(), image.getOrder(), image.getPath());
    }
}
