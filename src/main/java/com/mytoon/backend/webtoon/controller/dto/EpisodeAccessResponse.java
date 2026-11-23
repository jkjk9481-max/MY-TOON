package com.mytoon.backend.webtoon.controller.dto;

public record EpisodeAccessResponse(
        int episodeNumber,
        String title,
        boolean accessible,
        String reason // accessible이 true면 null, false면 "LOGIN_REQUIRED" 또는 "INK_REQUIRED"
) {
}
