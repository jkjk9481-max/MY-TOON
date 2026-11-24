package com.mytoon.backend.webtoon.controller.dto;

public record EpisodeAccessResponse(
        int episodeNumber,
        String title,
        boolean accessible, // 이 회차를 지금 이 사용자가 실제로 읽을 수 있는지
        String reason // accessible이 true면 null, false면 "LOGIN_REQUIRED" 또는 "INK_REQUIRED"
) {
}
