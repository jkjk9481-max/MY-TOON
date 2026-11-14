-- V4__create_webtoons.sql
-- 작품(Webtoon) 테이블. 제목은 작품끼리 중복 불가, 소개글은 나중에 채울 수 있도록 NULL 허용.

CREATE TABLE webtoons (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    author_name VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_webtoons_title UNIQUE (title)
);
