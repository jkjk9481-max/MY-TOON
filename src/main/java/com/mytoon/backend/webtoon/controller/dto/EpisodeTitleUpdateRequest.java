package com.mytoon.backend.webtoon.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record EpisodeTitleUpdateRequest(
        @NotBlank String title
) {
}
