package com.mytoon.backend.webtoon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;

// 독자용 회차 목록 조회 API: 비회원/회원별로 DRAFT/PREVIEW/PUBLIC 노출 규칙이 지켜지는지 실제 Postgres로 검증한다.
@SpringBootTest
@AutoConfigureMockMvc
class EpisodeControllerIntegrationTest {

    private static final String TITLE_PREFIX = "독자조회테스트-";

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

    private Webtoon newWebtoon() {
        return webtoonRepository.save(Webtoon.builder().title(TITLE_PREFIX + UUID.randomUUID()).authorName("도윤").build());
    }

    private Episode newEpisodeWithStatus(Webtoon webtoon, int number, String title, EpisodeStatus status) {
        return episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(number).title(title).status(status).build());
    }

    private String memberAuthHeader() {
        return "Bearer " + jwtTokenProvider.generateAccessToken(1L, Role.USER);
    }

    private ResultActions listEpisodes(Long webtoonId, String authHeader) throws Exception {
        var request = get("/api/webtoons/" + webtoonId + "/episodes");
        if (authHeader != null) {
            request = request.header("Authorization", authHeader);
        }
        return mockMvc.perform(request);
    }

    @Test
    @DisplayName("비회원도 PUBLIC 1화는 접근 가능으로 내려온다")
    void list_guest_firstPublicEpisode_accessible() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);

        listEpisodes(webtoon.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].episodeNumber").value(1))
                .andExpect(jsonPath("$[0].accessible").value(true))
                .andExpect(jsonPath("$[0].reason").doesNotExist());
    }

    @Test
    @DisplayName("비회원은 PUBLIC 2화부터 접근 불가(LOGIN_REQUIRED)로 내려온다")
    void list_guest_secondPublicEpisode_requiresLogin() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화", EpisodeStatus.PUBLIC);

        listEpisodes(webtoon.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].episodeNumber").value(2))
                .andExpect(jsonPath("$[1].accessible").value(false))
                .andExpect(jsonPath("$[1].reason").value("LOGIN_REQUIRED"));
    }

    @Test
    @DisplayName("회원은 PUBLIC 2화도 접근 가능하다")
    void list_member_secondPublicEpisode_accessible() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화", EpisodeStatus.PUBLIC);

        listEpisodes(webtoon.getId(), memberAuthHeader())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].accessible").value(true))
                .andExpect(jsonPath("$[1].reason").doesNotExist());
    }

    @Test
    @DisplayName("DRAFT 회차는 비회원 목록에서 완전히 빠진다")
    void list_guest_draftEpisode_hidden() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화 초안", EpisodeStatus.DRAFT);

        listEpisodes(webtoon.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("DRAFT 회차는 회원 목록에서도 완전히 빠진다")
    void list_member_draftEpisode_hidden() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화 초안", EpisodeStatus.DRAFT);

        listEpisodes(webtoon.getId(), memberAuthHeader())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PREVIEW 회차는 비회원 목록에서 빠진다")
    void list_guest_previewEpisode_hidden() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화 미리보기", EpisodeStatus.PREVIEW);

        listEpisodes(webtoon.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PREVIEW 회차는 회원 목록에 보이지만 잠겨있다(INK_REQUIRED)")
    void list_member_previewEpisode_visibleButLocked() throws Exception {
        Webtoon webtoon = newWebtoon();
        newEpisodeWithStatus(webtoon, 1, "1화", EpisodeStatus.PUBLIC);
        newEpisodeWithStatus(webtoon, 2, "2화 미리보기", EpisodeStatus.PREVIEW);

        listEpisodes(webtoon.getId(), memberAuthHeader())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].accessible").value(false))
                .andExpect(jsonPath("$[1].reason").value("INK_REQUIRED"));
    }

    @Test
    @DisplayName("존재하지 않는 작품이면 404")
    void list_webtoonNotFound_returns404() throws Exception {
        listEpisodes(0L, null)
                .andExpect(status().isNotFound());
    }
}
