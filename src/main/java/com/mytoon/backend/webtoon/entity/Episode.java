package com.mytoon.backend.webtoon.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "episodes",
        uniqueConstraints = {
                // 같은 작품 안에서 회차 번호/제목 중복 불가 (V5 마이그레이션의 제약 이름과 맞춘 것)
                @UniqueConstraint(name = "uq_episodes_webtoon_number", columnNames = {"webtoon_id", "episode_number"}),
                @UniqueConstraint(name = "uq_episodes_webtoon_title", columnNames = {"webtoon_id", "title"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "webtoon_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_episodes_webtoon")
    )
    private Webtoon webtoon;

    @Column(name = "episode_number", nullable = false)
    private int episodeNumber;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EpisodeStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    public void updateTitle(String title){
        this.title = title;
    }


}
