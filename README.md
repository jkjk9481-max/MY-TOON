# MY TOON

웹툰을 연재·감상하는 플랫폼입니다. 독자가 작품의 세계관을 탐험하고 이야기에 참여할 수 있도록 확장하는 것이 목표입니다.
실제 서비스 운영과 백엔드 개발자 포트폴리오를 함께 목표로 하는 프로젝트입니다.

- 웹툰의 스토리와 이미지는 ChatGPT·Claude와 함께 직접 제작하며, 초기에는 본인 작품 중심으로 운영합니다.
- 차별화 기능: 세계관 탐험(도감/지도/사건 기록), 순차 단서 수집, 선택형 특별 에피소드, 독자 추리·예측

> 현재 개발 중입니다. 아래 "구현 현황"에서 완료/예정 범위를 확인할 수 있습니다.

## 기술 스택

| 영역 | 사용 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Flyway, Gradle |
| Frontend | React, Vite, TypeScript |
| DevOps | Docker, GitHub Actions (예정), AWS (검토 중) |

## 구현 현황

### 완료 (자동화 테스트로 검증)

**인증**
- Google/Kakao 소셜 로그인 (프론트가 받은 authorization code를 백엔드 API로 전달, 최초 로그인 시 자동 가입)
- JWT Access Token(1시간) + Refresh Token(30일, DB 저장, 멀티 디바이스)
- Refresh Token Rotation, 로그아웃
- JWT 인증 필터와 역할(USER/ADMIN) 기반 접근 제어

**작품·회차 관리 (관리자 API)**
- 작품 등록/수정/삭제
- 회차 등록, 제목 수정, 상태 전환(DRAFT → PREVIEW → PUBLIC)

**프론트엔드**
- 로그인 흐름 뼈대, 메인/작품 상세/뷰어 화면 (mock 데이터 기반)

### 아직 검증하지 못한 부분
- 실제 Google/Kakao 서버를 호출하는 로그인은 테스트하지 않았습니다. (외부 응답을 흉내낸 테스트만 있음)
- 프론트엔드는 아직 백엔드와 연결되지 않았습니다. (mock 로그인 사용)

### 예정
- 회차 삭제, 독자용 조회 API, 웹툰 이미지 업로드, 뷰어, 비회원/회원 접근 권한

## 로드맵

| Phase | 범위 |
| --- | --- |
| 1 | 회원/소셜 로그인, 작품·회차 관리, 이미지 업로드, 뷰어, 접근 권한 |
| 2 | 잉크(가상 재화), 미리보기 회차 해금, 예약 무료 공개, 트랜잭션·동시성 처리 |
| 3 | 세계관 도감, 등장인물·지도, 독서 진행도, 순차 단서 획득 |
| 4 | 선택지, 분기형 스토리 |
| 5 | 독자 예측, 투표, 추리 게시판 |

장기적으로 다른 작가의 작품 등록과 실제 결제·정산 기능을 추가할 계획입니다.

## 실행 방법

### 사전 준비
- JDK 17
- PostgreSQL (로컬 `localhost:5432`에 `mytoon` 데이터베이스 생성)
- Node.js (프론트엔드 실행 시)

### 환경변수

| 이름 | 설명 |
| --- | --- |
| `DB_PASSWORD` | PostgreSQL 비밀번호 (사용자명은 `application.properties` 기준 `postgres`) |
| `JWT_SECRET` | JWT 서명 키 (HS256, **32자 이상**) |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` / `GOOGLE_REDIRECT_URI` | 구글 로그인용 |
| `KAKAO_CLIENT_ID` / `KAKAO_CLIENT_SECRET` / `KAKAO_REDIRECT_URI` | 카카오 로그인용 |

DB 접속 정보는 `src/main/resources/application.properties`에서 확인할 수 있고, 스키마는 Flyway 마이그레이션(`src/main/resources/db/migration`)이 관리합니다.

### 백엔드

```bash
./gradlew bootRun
```

### 테스트

실제 PostgreSQL을 사용하는 통합 테스트가 포함되어 있어 로컬 DB와 `DB_PASSWORD`, `JWT_SECRET` 환경변수가 필요합니다.

```bash
./gradlew test
```

### 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

## 프로젝트 구조

```
src/main/java/com/mytoon/backend
├── user      # 회원, 소셜 계정, 인증(로그인/Refresh Token)
├── webtoon   # 작품, 회차
├── oauth     # Google/Kakao 외부 OAuth2 연동
├── security  # JWT 발급·검증, 인증 필터
└── config    # SecurityConfig 등 전역 설정
frontend/     # React + Vite
```
