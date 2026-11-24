package com.mytoon.backend.webtoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.webtoon.entity.EpisodeImage;

public interface EpisodeImageRepository extends JpaRepository<EpisodeImage, Long> {

    List<EpisodeImage> findByEpisodeId(Long episodeId);

    long countByEpisodeId(Long episodeId);

    void deleteByEpisodeId(Long episodeId);
}
