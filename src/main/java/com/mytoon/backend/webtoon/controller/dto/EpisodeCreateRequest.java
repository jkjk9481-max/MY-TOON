package com.mytoon.backend.webtoon.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record EpisodeCreateRequest(
        int episodeNumber,
        @NotBlank String title
) {
}
