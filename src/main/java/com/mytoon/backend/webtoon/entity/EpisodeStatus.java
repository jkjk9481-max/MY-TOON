package com.mytoon.backend.webtoon.entity;

// 회차 공개 상태. 값만 정의해 두고, PREVIEW의 잉크 구매 동작은 Phase 2에서 구현한다.
public enum EpisodeStatus {
    DRAFT,   // 임시저장 (독자에게 숨김)
    PREVIEW, // 미리보기 (잉크로 구매 가능)
    PUBLIC;  // 무료 공개

    // 앞으로만 이동 가능 (DRAFT -> PREVIEW -> PUBLIC, DRAFT -> PUBLIC 직행 허용). 같은 상태·뒤로 이동은 불가.
    // 선언 순서(ordinal)에 의존하므로 값을 추가/재배치할 때 이 규칙도 함께 확인할 것.
    public boolean canMoveTo(EpisodeStatus next) {
        return next.ordinal() > this.ordinal();
    }
}
