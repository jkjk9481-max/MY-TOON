package com.mytoon.backend.user;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint( // 이 어노테이션이 실제로 DB에 유니크 제약을 만들진않는다
                name = "uq_social_accounts_provider_user", // 이 컬럼 조합은 유니크해야한다
                columnNames = {"provider", "provider_user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 여러개의 계정이 하나의 User에 속한다
    @JoinColumn(
            name = "user_id", // FK 컬럼  == user_id 컬럼으로 저장해라
            nullable = false, // 계정은 반드시 User에 연결해야함
            foreignKey = @ForeignKey(name = "fk_social_accounts_user") // V1_SQL에서 지정한 FK 제약조건 이름과 맞춘것
    )
    private User user;

    @Enumerated(EnumType.STRING) // enum을 DB에 저장할때 문자열 그대로 저장하라는 뜻
    @Column(nullable = false, length = 20) // 이 자바 필드를 DB 테이블의 어떤 컬럼으로 , 어떤 제약으로 매핑할지 지정하는 JPA 어노테이션
    private Provider provider;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;
}
