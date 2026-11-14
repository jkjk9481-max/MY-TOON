package com.mytoon.backend.webtoon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.mytoon.backend.webtoon.entity.Webtoon;

// 실제 Postgres + Flyway(V4)로 돌려서, 엔티티 매핑과 DB 제약(UNIQUE, NOT NULL)이 진짜 동작하는지 확인한다.
@SpringBootTest
class WebtoonRepositoryTest {

    @Autowired
    private WebtoonRepository webtoonRepository;

    private final String uniqueTitle = "테스트작품-" + UUID.randomUUID();

    @AfterEach
    void cleanUp() {
        webtoonRepository.findAll().stream()
                .filter(w -> w.getTitle().startsWith("테스트작품-"))
                .forEach(webtoonRepository::delete);
    }

    @Test
    @DisplayName("소개글 없이도 저장되고, 등록일이 자동으로 채워진다")
    void save_withoutDescription_succeedsAndSetsCreatedAt() {
        Webtoon saved = webtoonRepository.save(
                Webtoon.builder().title(uniqueTitle).authorName("도윤").build());

        Webtoon found = webtoonRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo(uniqueTitle);
        assertThat(found.getDescription()).isNull();
        assertThat(found.getAuthorName()).isEqualTo("도윤");
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("같은 제목의 작품을 또 저장하면 DB 제약 때문에 실패한다")
    void save_duplicateTitle_throwsDataIntegrityViolation() {
        webtoonRepository.saveAndFlush(
                Webtoon.builder().title(uniqueTitle).authorName("도윤").build());

        assertThatThrownBy(() -> webtoonRepository.saveAndFlush(
                Webtoon.builder().title(uniqueTitle).authorName("다른작가").build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("제목이 다르면 같은 작가의 작품을 여러 개 저장할 수 있다")
    void save_differentTitles_bothSucceed() {
        webtoonRepository.saveAndFlush(
                Webtoon.builder().title(uniqueTitle).authorName("도윤").build());
        Webtoon second = webtoonRepository.saveAndFlush(
                Webtoon.builder().title(uniqueTitle + "2").authorName("도윤").build());

        assertThat(second.getId()).isNotNull();
    }

    @Test
    @DisplayName("제목이나 작가명이 비어 있으면(null) 저장에 실패한다")
    void save_nullTitleOrAuthor_throwsDataIntegrityViolation() {
        assertThatThrownBy(() -> webtoonRepository.saveAndFlush(
                Webtoon.builder().authorName("도윤").build()))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> webtoonRepository.saveAndFlush(
                Webtoon.builder().title(uniqueTitle).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
