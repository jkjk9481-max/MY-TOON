package com.mytoon.backend.webtoon.entity;

// 회차 공개 상태. 값만 정의해 두고, PREVIEW의 잉크 구매 동작은 Phase 2에서 구현한다.
public enum EpisodeStatus {
    DRAFT,   // 임시저장 (독자에게 숨김)
    PREVIEW, // 미리보기 (잉크로 구매 가능)
    PUBLIC   // 무료 공개
}
