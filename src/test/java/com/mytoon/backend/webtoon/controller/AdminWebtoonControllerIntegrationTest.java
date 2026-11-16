package com.mytoon.backend.webtoon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

// 실제 Postgres로 Controller->Service->Repository 전체 경로를 돌려서, 등록/수정/삭제의
// 비즈니스 규칙(제목 중복 409, 회차 남아있으면 삭제 거절 409, 존재하지 않는 id 404)이 실제로 지켜지는지 확인한다.
@SpringBootTest
@AutoConfigureMockMvc
class AdminWebtoonControllerIntegrationTest {

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
        // FK RESTRICT 때문에 회차를 먼저 지우고 작품을 지운다.
        jdbcTemplate.update(
                "DELETE FROM episodes WHERE webtoon_id IN (SELECT id FROM webtoons WHERE title LIKE '" + TITLE_PREFIX + "%')");
        webtoonRepository.findAll().stream()
                .filter(w -> w.getTitle().startsWith(TITLE_PREFIX))
                .forEach(webtoonRepository::delete);
    }

    private String adminAuthHeader() {
        return "Bearer " + jwtTokenProvider.generateAccessToken(1L, Role.ADMIN);
    }

    private String uniqueTitle() {
        return TITLE_PREFIX + UUID.randomUUID();
    }

    @Test
    @DisplayName("정상 등록이면 201과 등록된 작품 정보를 반환한다")
    void create_valid_returns201() throws Exception {
        String title = uniqueTitle();

        mockMvc.perform(post("/api/admin/webtoons")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"authorName\":\"도윤\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title)) // title필드를 꺼내서 그 값이 기대한 값과 같은지 비교
                .andExpect(jsonPath("$.authorName").value("도윤"))
                .andExpect(jsonPath("$.id").isNotEmpty()); // "값이 있기만하면 OK , 정확한 값은 안 따짐"
    }

    @Test
    @DisplayName("이미 존재하는 제목으로 등록하면 409")
    void create_duplicateTitle_returns409() throws Exception {
        String title = uniqueTitle();
        webtoonRepository.save(Webtoon.builder().title(title).authorName("도윤").build());

        mockMvc.perform(post("/api/admin/webtoons")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"authorName\":\"다른작가\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("보낸 필드만 부분 수정된다")
    void update_partial_updatesOnlySentFields() throws Exception {
        Webtoon webtoon = webtoonRepository.save(
                Webtoon.builder().title(uniqueTitle()).description("원래 소개글").authorName("도윤").build());

        mockMvc.perform(patch("/api/admin/webtoons/" + webtoon.getId())
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"수정된 소개글\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(webtoon.getTitle()))
                .andExpect(jsonPath("$.description").value("수정된 소개글"))
                .andExpect(jsonPath("$.authorName").value("도윤"));
    }

    @Test
    @DisplayName("다른 작품과 제목이 중복되게 수정하면 409")
    void update_duplicateTitleWithAnotherWebtoon_returns409() throws Exception {
        Webtoon other = webtoonRepository.save(Webtoon.builder().title(uniqueTitle()).authorName("도윤").build());
        Webtoon target = webtoonRepository.save(Webtoon.builder().title(uniqueTitle()).authorName("도윤").build());

        mockMvc.perform(patch("/api/admin/webtoons/" + target.getId())
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + other.getTitle() + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("존재하지 않는 작품을 수정하면 404")
    void update_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/api/admin/webtoons/0")
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"아무거나\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 작품을 삭제하면 404")
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/admin/webtoons/0")
                        .header("Authorization", adminAuthHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회차가 남아있는 작품을 삭제하면 409")
    void delete_withRemainingEpisodes_returns409() throws Exception {
        Webtoon webtoon = webtoonRepository.save(Webtoon.builder().title(uniqueTitle()).authorName("도윤").build());
        episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(1).title("1화").status(EpisodeStatus.DRAFT).build());

        mockMvc.perform(delete("/api/admin/webtoons/" + webtoon.getId())
                        .header("Authorization", adminAuthHeader()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회차가 없는 작품을 삭제하면 204")
    void delete_withoutEpisodes_returns204() throws Exception {
        Webtoon webtoon = webtoonRepository.save(Webtoon.builder().title(uniqueTitle()).authorName("도윤").build());

        mockMvc.perform(delete("/api/admin/webtoons/" + webtoon.getId())
                        .header("Authorization", adminAuthHeader()))
                .andExpect(status().isNoContent());
    }
}
