-- V3__add_refresh_token_revocation.sql
-- refresh_tokens에 폐기(rotation/탈취 대응) 상태 컬럼 추가

ALTER TABLE refresh_tokens
    ADD COLUMN revoked    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN revoked_at TIMESTAMPTZ;
