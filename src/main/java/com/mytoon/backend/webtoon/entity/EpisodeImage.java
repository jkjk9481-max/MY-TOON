package com.mytoon.backend.webtoon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "episode_images",
        uniqueConstraints = {
                // 같은 회차 안에서 같은 순서 번호 중복 불가 (V6 마이그레이션의 제약 이름과 맞춘 것)
                @UniqueConstraint(name = "uq_episode_images_episode_order", columnNames = {"episode_id", "image_order"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EpisodeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "episode_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_episode_images_episode")
    )
    private Episode episode;

    @Column(name = "image_order", nullable = false)
    private int order; // 업로드한 순서 = 컷 순서

    @Column(nullable = false, length = 255)
    private String path; // 업로드 루트 기준 상대 경로
}
