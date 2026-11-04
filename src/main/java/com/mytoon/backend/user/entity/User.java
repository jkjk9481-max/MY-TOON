package com.mytoon.backend.user.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // // @Builder가 호출할 전체 필드 생성자. build() 이외에는 못 쓰게 private으로 막음
@Builder // 디자인 패턴을 자동으로 만들어주는 어노테이션 , 생성자 대신 쓰는것
// User user = User.builder()
//        .nickname("홍길동")
//        .role(Role.USER)
//        .status(UserStatus.ACTIVE)
//        .build(); --> 이걸 자동생성해줌
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @CreationTimestamp // 이 엔티티가 처음 INSERT 될 떄 , 이 필드에 현재 시각을 자동으로 넣어달라는 어노테이션
    // 필드값을 채워주는 훅(hook)정도로 이해
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
