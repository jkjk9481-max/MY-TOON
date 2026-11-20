package com.mytoon.backend.webtoon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    private Episode newEpisode(Webtoon webtoon, int number, String title) {
        return episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(number).title(title).status(EpisodeStatus.DRAFT).build());
    }

    private org.springframework.test.web.servlet.ResultActions patchTitle(Long webtoonId, Long episodeId, String title) throws Exception {
        return mockMvc.perform(patch("/api/admin/webtoons/" + webtoonId + "/episodes/" + episodeId)
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"" + title + "\"}"));
    }

    @Test
    @DisplayName("제목을 정상 수정하면 200과 바뀐 제목을 반환한다")
    void updateTitle_success_returns200() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, "프롤로그");

        patchTitle(webtoon.getId(), ep.getId(), "새 제목")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새 제목"));
    }

    @Test
    @DisplayName("자기 제목 그대로 수정해도 200 (자기 자신은 중복 검사에서 제외)")
    void updateTitle_sameTitleAsSelf_returns200() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, "프롤로그");

        patchTitle(webtoon.getId(), ep.getId(), "프롤로그")
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("같은 작품의 다른 회차와 제목이 겹치면 409")
    void updateTitle_duplicateWithOtherEpisode_returns409() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep1 = newEpisode(webtoon, 1, "프롤로그");
        newEpisode(webtoon, 2, "새로운 시작");

        patchTitle(webtoon.getId(), ep1.getId(), "새로운 시작")
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("존재하지 않는 회차면 404")
    void updateTitle_episodeNotFound_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();

        patchTitle(webtoon.getId(), 0L, "제목")
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 작품이면 404")
    void updateTitle_webtoonNotFound_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, "프롤로그");

        patchTitle(0L, ep.getId(), "제목")
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회차가 다른 작품 소속이면 404 (작품 범위 검사)")
    void updateTitle_episodeOfOtherWebtoon_returns404() throws Exception {
        Webtoon webtoonA = newWebtoon();
        Webtoon webtoonB = newWebtoon();
        Episode epOfA = newEpisode(webtoonA, 1, "프롤로그");

        patchTitle(webtoonB.getId(), epOfA.getId(), "제목")
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("제목이 공백이면 400")
    void updateTitle_blankTitle_returns400() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, "프롤로그");

        patchTitle(webtoon.getId(), ep.getId(), "   ")
                .andExpect(status().isBadRequest());
    }
}
