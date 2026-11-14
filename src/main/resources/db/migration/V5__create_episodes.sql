-- V5__create_episodes.sql
-- 회차(Episode) 테이블.
-- - 회차 번호/제목은 "작품 안에서" 중복 불가 (작품이 다르면 같은 번호/제목 가능)
-- - 회차 번호는 1부터 (번호 건너뛰기 금지는 DB가 아니라 관리자 API에서 검사)
-- - 작품 삭제는 회차가 남아 있으면 거절 (ON DELETE RESTRICT)
-- - 삭제 컬럼 없음: 하드 삭제 + 마지막 회차만 삭제 가능 (규칙은 API 단계에서 구현)

CREATE TABLE episodes (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    webtoon_id     BIGINT       NOT NULL,
    episode_number INT          NOT NULL,
    title          VARCHAR(100) NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_episodes_webtoon FOREIGN KEY (webtoon_id) REFERENCES webtoons (id) ON DELETE RESTRICT,
    CONSTRAINT uq_episodes_webtoon_number UNIQUE (webtoon_id, episode_number),
    CONSTRAINT uq_episodes_webtoon_title  UNIQUE (webtoon_id, title),
    CONSTRAINT ck_episodes_number CHECK (episode_number >= 1),
    CONSTRAINT ck_episodes_status CHECK (status IN ('DRAFT', 'PREVIEW', 'PUBLIC'))
);
