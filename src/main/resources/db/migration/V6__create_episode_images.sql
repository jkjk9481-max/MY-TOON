-- V6__create_episode_images.sql
-- 회차(Episode)에 속한 이미지 컷. 업로드한 순서가 곧 컷 순서(image_order)이다.
-- - 회차가 DRAFT일 때만 수정(업로드) 가능하다는 규칙은 서비스 계층에서 검사한다 (DB는 모름)
-- - 재업로드는 전체 교체 방식 (기존 행을 지우고 새로 넣음)
-- - Episode가 삭제되면 같이 삭제된다 (ON DELETE CASCADE). 실제 파일 삭제는 애플리케이션이 처리

CREATE TABLE episode_images (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    episode_id  BIGINT       NOT NULL,
    image_order INT          NOT NULL,
    path        VARCHAR(255) NOT NULL,
    CONSTRAINT fk_episode_images_episode FOREIGN KEY (episode_id) REFERENCES episodes (id) ON DELETE CASCADE,
    CONSTRAINT uq_episode_images_episode_order UNIQUE (episode_id, image_order),
    CONSTRAINT ck_episode_images_order CHECK (image_order >= 1)
);
