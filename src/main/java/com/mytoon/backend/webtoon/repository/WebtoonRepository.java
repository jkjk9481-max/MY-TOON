package com.mytoon.backend.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.webtoon.entity.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);
}
