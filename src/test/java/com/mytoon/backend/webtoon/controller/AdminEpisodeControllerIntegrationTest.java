package com.mytoon.backend.webtoon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;

// 실제 Postgres로 Controller->Service->Repository 전체 경로를 돌려서, 회차 등록의
// 비즈니스 규칙(번호 건너뛰기 금지 409, 제목 중복 409, 존재하지 않는 작품 404)이 실제로 지켜지는지 확인한다.
@SpringBootTest
@AutoConfigureMockMvc
class AdminEpisodeControllerIntegrationTest {

    private static final String TITLE_PREFIX = "테스트작품-";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update(
                "DELETE FROM episodes WHERE webtoon_id IN (SELECT id FROM webtoons WHERE title LIKE '" + TITLE_PREFIX + "%')");
        webtoonRepository.findAll().stream()
                .filter(w -> w.getTitle().startsWith(TITLE_PREFIX))
                .forEach(webtoonRepository::delete);
    }

    private String adminAuthHeader() {
        return "Bearer " + jwtTokenProvider.generateAccessToken(1L, Role.ADMIN);
    }

    private Webtoon newWebtoon() {
        return webtoonRepository.save(Webtoon.builder().title(TITLE_PREFIX + UUID.randomUUID()).authorName("도윤").build());
    }

    @Test
    @DisplayName("1화를 정상 등록하면 201과 DRAFT 상태를 반환한다")
    void create_firstEpisode_returns201AndDraft() throws Exception {
        Webtoon webtoon = newWebtoon();

        mockMvc.perform(post("/api/admin/webtoons/" + webtoon.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":1,\"title\":\"프롤로그\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.episodeNumber").value(1))
                .andExpect(jsonPath("$.title").value("프롤로그"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.webtoonId").value(webtoon.getId()));
    }

    @Test
    @DisplayName("존재하지 않는 작품에 등록하면 404")
    void create_webtoonNotFound_returns404() throws Exception {
        mockMvc.perform(post("/api/admin/webtoons/0/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":1,\"title\":\"프롤로그\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("마지막 번호 + 1이 아니면 409 (건너뛰기)")
    void create_skippedNumber_returns409() throws Exception {
        Webtoon webtoon = newWebtoon();
        episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(1).title("1화").status(EpisodeStatus.DRAFT).build());

        mockMvc.perform(post("/api/admin/webtoons/" + webtoon.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":3,\"title\":\"3화\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("이미 쓰인 번호를 다시 등록하면 409")
    void create_duplicateNumber_returns409() throws Exception {
        Webtoon webtoon = newWebtoon();
        episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(1).title("1화").status(EpisodeStatus.DRAFT).build());

        mockMvc.perform(post("/api/admin/webtoons/" + webtoon.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":1,\"title\":\"또 1화\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("같은 작품 안에서 제목이 중복되면 409")
    void create_duplicateTitleInSameWebtoon_returns409() throws Exception {
        Webtoon webtoon = newWebtoon();
        episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(1).title("프롤로그").status(EpisodeStatus.DRAFT).build());

        mockMvc.perform(post("/api/admin/webtoons/" + webtoon.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":2,\"title\":\"프롤로그\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("다른 작품이면 같은 제목이어도 등록할 수 있다")
    void create_sameTitleInDifferentWebtoon_returns201() throws Exception {
        Webtoon webtoonA = newWebtoon();
        Webtoon webtoonB = newWebtoon();
        episodeRepository.save(Episode.builder()
                .webtoon(webtoonA).episodeNumber(1).title("프롤로그").status(EpisodeStatus.DRAFT).build());

        mockMvc.perform(post("/api/admin/webtoons/" + webtoonB.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":1,\"title\":\"프롤로그\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("제목이 공백이면 400")
    void create_blankTitle_returns400() throws Exception {
        Webtoon webtoon = newWebtoon();

        mockMvc.perform(post("/api/admin/webtoons/" + webtoon.getId() + "/episodes")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\":1,\"title\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }
}
