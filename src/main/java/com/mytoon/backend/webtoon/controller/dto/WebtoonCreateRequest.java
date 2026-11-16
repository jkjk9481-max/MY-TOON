package com.mytoon.backend.webtoon.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record WebtoonCreateRequest(
        @NotBlank String title,
        String description,
        @NotBlank String authorName
) {
}
