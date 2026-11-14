package com.mytoon.backend.webtoon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.entity.Webtoon;

// 실제 Postgres + Flyway(V5)로 돌려서, 엔티티 매핑과 DB 제약(UNIQUE, CHECK, FK RESTRICT)이 진짜 동작하는지 확인한다.
@SpringBootTest
class EpisodeRepositoryTest {

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Webtoon webtoonA;
    private Webtoon webtoonB;

    @BeforeEach
    void setUp() {
        String prefix = "테스트작품-" + UUID.randomUUID();
        webtoonA = webtoonRepository.save(Webtoon.builder().title(prefix + "A").authorName("도윤").build());
        webtoonB = webtoonRepository.save(Webtoon.builder().title(prefix + "B").authorName("도윤").build());
    }

    @AfterEach
    void cleanUp() {
        // FK RESTRICT 때문에 회차를 먼저 지우고 작품을 지워야 한다.
        // (Episode.webtoon은 LAZY라 세션 밖에서 title을 못 읽으므로, 접두사 기준으로 SQL로 한 번에 지운다)
        jdbcTemplate.update(
                "DELETE FROM episodes WHERE webtoon_id IN (SELECT id FROM webtoons WHERE title LIKE '테스트작품-%')");
        webtoonRepository.findAll().stream()
                .filter(w -> w.getTitle().startsWith("테스트작품-"))
                .forEach(webtoonRepository::delete);
    }

    private Episode episode(Webtoon webtoon, int number, String title) {
        return Episode.builder()
                .webtoon(webtoon).episodeNumber(number).title(title).status(EpisodeStatus.DRAFT).build();
    }

    @Test
    @DisplayName("회차를 저장하면 상태가 문자열로 저장되고 등록일이 자동으로 채워진다")
    void save_succeedsAndSetsCreatedAt() {
        Episode saved = episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));

        Episode found = episodeRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getEpisodeNumber()).isEqualTo(1);
        assertThat(found.getTitle()).isEqualTo("프롤로그");
        assertThat(found.getStatus()).isEqualTo(EpisodeStatus.DRAFT);
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("같은 작품에 같은 회차 번호를 또 저장하면 실패한다")
    void save_duplicateNumberInSameWebtoon_fails() {
        episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));

        assertThatThrownBy(() -> episodeRepository.saveAndFlush(episode(webtoonA, 1, "다른 제목")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("같은 작품에 같은 회차 제목을 또 저장하면 실패한다")
    void save_duplicateTitleInSameWebtoon_fails() {
        episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));

        assertThatThrownBy(() -> episodeRepository.saveAndFlush(episode(webtoonA, 2, "프롤로그")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("다른 작품이면 같은 회차 번호와 제목을 쓸 수 있다")
    void save_sameNumberAndTitleInDifferentWebtoons_succeeds() {
        episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));
        Episode other = episodeRepository.saveAndFlush(episode(webtoonB, 1, "프롤로그"));

        assertThat(other.getId()).isNotNull();
    }

    @Test
    @DisplayName("회차 번호가 1보다 작으면(0, 음수) 저장에 실패한다")
    void save_numberLessThanOne_fails() {
        assertThatThrownBy(() -> episodeRepository.saveAndFlush(episode(webtoonA, 0, "0화")))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> episodeRepository.saveAndFlush(episode(webtoonA, -1, "음수화")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("제목이나 상태가 비어 있으면(null) 저장에 실패한다")
    void save_nullTitleOrStatus_fails() {
        assertThatThrownBy(() -> episodeRepository.saveAndFlush(
                Episode.builder().webtoon(webtoonA).episodeNumber(1).status(EpisodeStatus.DRAFT).build()))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> episodeRepository.saveAndFlush(
                Episode.builder().webtoon(webtoonA).episodeNumber(1).title("제목").build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회차가 남아 있는 작품을 삭제하면 FK RESTRICT 때문에 실패한다")
    void deleteWebtoon_withEpisodes_failsByRestrict() {
        episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));

        assertThatThrownBy(() -> webtoonRepository.deleteById(webtoonA.getId()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("회차를 모두 지운 뒤에는 작품을 삭제할 수 있다")
    void deleteWebtoon_afterEpisodesRemoved_succeeds() {
        Episode saved = episodeRepository.saveAndFlush(episode(webtoonA, 1, "프롤로그"));
        episodeRepository.deleteById(saved.getId());
        episodeRepository.flush();

        webtoonRepository.deleteById(webtoonA.getId());
        webtoonRepository.flush();

        assertThat(webtoonRepository.findById(webtoonA.getId())).isEmpty();
    }
}
