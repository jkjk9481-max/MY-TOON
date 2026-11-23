package com.mytoon.backend.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mytoon.backend.webtoon.entity.Episode;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    boolean existsByWebtoonId(Long webtoonId);

    boolean existsByWebtoonIdAndTitle(Long webtoonId, String title);

    // 아직 회차가 하나도 없으면 0을 돌려줘서, 호출하는 쪽에서 "마지막 번호 + 1 = 1"이 자연스럽게 맞게 한다.
    @Query("SELECT COALESCE(MAX(e.episodeNumber), 0) FROM Episode e WHERE e.webtoon.id = :webtoonId")
    int findMaxEpisodeNumberByWebtoonId(@Param("webtoonId") Long webtoonId);


    // 자기 자신을 제외하고 비교
    boolean existsByWebtoonIdAndTitleAndIdNot(Long webtoonId , String title, Long id);


    // Optional -> 이 값은 없을수도있다
    Optional<Episode> findByIdAndWebtoonId(Long episodeId , Long webtoonId);

    // 독자용 회차 목록 조회: 번호 순서대로
    List<Episode> findByWebtoonIdOrderByEpisodeNumberAsc(Long webtoonId);
}
