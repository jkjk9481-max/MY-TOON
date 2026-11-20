package com.mytoon.backend.webtoon.controller.dto;

import com.mytoon.backend.webtoon.entity.EpisodeStatus;

import jakarta.validation.constraints.NotNull;

public record EpisodeStatusUpdateRequest(
        @NotNull EpisodeStatus status
) {
}
