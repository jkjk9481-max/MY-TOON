package com.mytoon.backend.webtoon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.webtoon.entity.EpisodeImage;

public interface EpisodeImageRepository extends JpaRepository<EpisodeImage, Long> {

    List<EpisodeImage> findByEpisodeId(Long episodeId);

    long countByEpisodeId(Long episodeId);

    // 독자용 뷰어: 컷 순서대로
    List<EpisodeImage> findByEpisodeIdOrderByOrderAsc(Long episodeId);

    Optional<EpisodeImage> findByEpisodeIdAndOrder(Long episodeId, int order);

    void deleteByEpisodeId(Long episodeId);
}
