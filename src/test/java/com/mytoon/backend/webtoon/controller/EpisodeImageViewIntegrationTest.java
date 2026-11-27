package com.mytoon.backend.webtoon.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.test.web.servlet.ResultActions;

import com.mytoon.backend.security.JwtTokenProvider;
import com.mytoon.backend.user.entity.Role;
import com.mytoon.backend.webtoon.entity.Episode;
import com.mytoon.backend.webtoon.entity.EpisodeImage;
import com.mytoon.backend.webtoon.entity.EpisodeStatus;
import com.mytoon.backend.webtoon.entity.Webtoon;
import com.mytoon.backend.webtoon.repository.EpisodeImageRepository;
import com.mytoon.backend.webtoon.repository.EpisodeRepository;
import com.mytoon.backend.webtoon.repository.WebtoonRepository;
import com.mytoon.backend.webtoon.service.EpisodeImageStorage;

// 독자용 회차 이미지 조회 API: 목록/파일 두 API 모두에 접근 규칙이 적용되는지 실제 Postgres와 파일로 검증한다.
@SpringBootTest(properties = "app.upload.root-dir=build/test-uploads")
@AutoConfigureMockMvc
class EpisodeImageViewIntegrationTest {

    private static final String TITLE_PREFIX = "이미지조회테스트-";
    private static final byte[] IMAGE_BYTES = {1, 2, 3, 4};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private EpisodeImageRepository episodeImageRepository;

    @Autowired
    private EpisodeImageStorage episodeImageStorage;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        webtoonRepository.findAll().stream()
                .filter(w -> w.getTitle().startsWith(TITLE_PREFIX))
                .forEach(w -> {
                    episodeRepository.findByWebtoonIdOrderByEpisodeNumberAsc(w.getId())
                            .forEach(e -> episodeImageStorage.deleteEpisodeDir(w.getId(), e.getId()));
                    // episode_images 행은 ON DELETE CASCADE로 함께 지워진다
                    jdbcTemplate.update("DELETE FROM episodes WHERE webtoon_id = ?", w.getId());
                    webtoonRepository.delete(w);
                });
    }

    private Webtoon newWebtoon() {
        return webtoonRepository.save(Webtoon.builder().title(TITLE_PREFIX + UUID.randomUUID()).authorName("도윤").build());
    }

    private Episode newEpisode(Webtoon webtoon, int number, EpisodeStatus status) {
        return episodeRepository.save(Episode.builder()
                .webtoon(webtoon).episodeNumber(number).title(number + "화").status(status).build());
    }

    private void newImage(Episode episode, Long webtoonId, int order) throws IOException {
        String path = saveImageRow(episode, webtoonId, order);
        Path file = episodeImageStorage.resolve(path);
        Files.createDirectories(file.getParent());
        Files.write(file, IMAGE_BYTES);
    }

    // 파일 없이 DB 행만 만든다
    private String saveImageRow(Episode episode, Long webtoonId, int order) {
        String path = episodeImageStorage.relativeEpisodeDir(webtoonId, episode.getId()) + "/" + order + ".png";
        episodeImageRepository.save(EpisodeImage.builder().episode(episode).order(order).path(path).build());
        return path;
    }

    private ResultActions request(Long webtoonId, Long episodeId, String suffix, boolean member) throws Exception {
        var request = get("/api/webtoons/" + webtoonId + "/episodes/" + episodeId + "/images" + suffix);
        if (member) {
            request = request.header("Authorization", "Bearer " + jwtTokenProvider.generateAccessToken(1L, Role.USER));
        }
        return mockMvc.perform(request);
    }

    // 목록 API와 파일 API가 같은 상태 코드를 돌려주는지 함께 확인
    private void assertBoth(Long webtoonId, Long episodeId, boolean member, int expected) throws Exception {
        request(webtoonId, episodeId, "", member).andExpect(status().is(expected));
        request(webtoonId, episodeId, "/1", member).andExpect(status().is(expected));
    }

    @Test
    @DisplayName("비회원은 PUBLIC 1화의 목록과 파일을 받을 수 있다")
    void guest_firstPublicEpisode_returns200() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.PUBLIC);
        newImage(ep, webtoon.getId(), 2);
        newImage(ep, webtoon.getId(), 1);

        request(webtoon.getId(), ep.getId(), "", false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].order").value(1))
                .andExpect(jsonPath("$[1].order").value(2))
                .andExpect(jsonPath("$[0].path").doesNotExist());

        request(webtoon.getId(), ep.getId(), "/1", false)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(IMAGE_BYTES));
    }

    @Test
    @DisplayName("비회원의 PUBLIC 2화 요청은 401")
    void guest_secondPublicEpisode_returns401() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 2, EpisodeStatus.PUBLIC);
        newImage(ep, webtoon.getId(), 1);

        assertBoth(webtoon.getId(), ep.getId(), false, 401);
    }

    @Test
    @DisplayName("회원은 PUBLIC 2화를 볼 수 있다")
    void member_secondPublicEpisode_returns200() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 2, EpisodeStatus.PUBLIC);
        newImage(ep, webtoon.getId(), 1);

        assertBoth(webtoon.getId(), ep.getId(), true, 200);
    }

    @Test
    @DisplayName("PREVIEW는 1화라도 비회원은 401, 회원은 403")
    void previewEpisode_guest401_member403() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.PREVIEW);
        newImage(ep, webtoon.getId(), 1);

        assertBoth(webtoon.getId(), ep.getId(), false, 401);
        assertBoth(webtoon.getId(), ep.getId(), true, 403);
    }

    @Test
    @DisplayName("DRAFT는 비회원/회원 모두 404")
    void draftEpisode_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.DRAFT);
        newImage(ep, webtoon.getId(), 1);

        assertBoth(webtoon.getId(), ep.getId(), false, 404);
        assertBoth(webtoon.getId(), ep.getId(), true, 404);
    }

    @Test
    @DisplayName("다른 작품 소속 회차는 404")
    void episodeOfOtherWebtoon_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();
        Webtoon other = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.PUBLIC);
        newImage(ep, webtoon.getId(), 1);

        assertBoth(other.getId(), ep.getId(), true, 404);
    }

    @Test
    @DisplayName("없는 순서 번호는 404")
    void unknownOrder_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.PUBLIC);
        newImage(ep, webtoon.getId(), 1);

        request(webtoon.getId(), ep.getId(), "/2", false).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DB 기록은 있는데 파일이 없으면 404")
    void missingFile_returns404() throws Exception {
        Webtoon webtoon = newWebtoon();
        Episode ep = newEpisode(webtoon, 1, EpisodeStatus.PUBLIC);
        saveImageRow(ep, webtoon.getId(), 1);

        request(webtoon.getId(), ep.getId(), "/1", false).andExpect(status().isNotFound());
    }
}
