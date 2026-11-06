# MY TOON

웹툰을 연재·감상하는 플랫폼. 독자가 작품 세계관을 탐험하고 이야기에 참여할 수 있도록 확장하는 것이 목표.
실제 서비스 운영 + 백엔드 개발자 취업 포트폴리오를 동시에 목표로 한다. (소유자: 도윤)

- 웹툰의 스토리/이미지는 ChatGPT·Claude와 함께 직접 제작, 초기에는 본인 작품 중심으로 운영
- 기획 문서: https://claude.ai/artifact/4AkXR1FUVqT5q4AVVDpniv
- 진행 현황 문서: https://claude.ai/artifact/VA9XmKi56TsAK4QhkQZjVi

## 기술 스택
- Backend: Java 17, Spring Boot, Spring Data JPA, Spring Security/OAuth2, PostgreSQL, Flyway
- Frontend: React, Vite
- DevOps: Docker, GitHub Actions, CI/CD (추후 AWS 검토)
- 이미지 저장: 초기 로컬/개발 환경 → 이후 클라우드 저장소

## 작업 지침 (AI와 협업 원칙)
- 순서: 요구사항 확인 → 내 설계 판단 → AI 설계 리뷰 → 코드 생성 → 내 코드 검토 → 테스트 → 리팩터링 → 커밋
- 핵심 비즈니스 로직과 정책(삭제 정책, 숫자 기준 등)은 사용자가 결정한다. AI가 임의로 확정하지 않는다.
- 요청하지 않은 기능을 임의로 구현하거나 미확정 정책을 확정하지 않는다.
- 단순 JPA 매핑, DTO, Controller, 반복 CRUD는 AI가 바로 생성해도 된다.
- 코드 생성 후에는 동작 원리, 예외 상황, 트랜잭션, 테스트 방법을 설명한다.
- 코드 설명 후에는 이해도 확인 질문을 던지고, 사용자가 답할 수 있는지 확인한 다음에 다음 단계로 넘어간다.
- 테스트는 실제로 실행해서 검증한다. 실행하지 않았으면 성공했다고 주장하지 않는다.
- 기존 코드 구조/설정과 충돌하는 변경을 임의로 만들지 않는다.
- 기능 하나가 끝날 때마다 테스트하고 커밋 단위를 제안한다.
- 다음에 뭘 할지는 사용자가 로드맵 기준으로 먼저 정한다. AI에게 묻는 건 트레이드오프 판단이 안 설 때로 한정한다.
- 설명은 초보자도 이해하기 쉽게, 비유와 예시를 섞어서 한다.

## 확정된 설계 결정 (Phase 1 로그인)
- 회원가입/로그인은 Google/Kakao 소셜 로그인만 사용 (아이디·비밀번호 없음). 최초 로그인 시 자동 가입.
- 로그인 아키텍처: 프론트(React)가 구글/카카오로 직접 리다이렉트해 code를 받고, 백엔드 REST API로 전달.
  Spring Security의 oauth2Login 자동 리다이렉트 방식은 쓰지 않는다.
- JWT 방식 (모바일 앱 확장 고려, 서버 세션 사용 안 함)
- Access Token 1시간. Refresh Token은 30일 슬라이딩 만료 + Rotation, DB 저장, 멀티 디바이스 허용 (User 1 : RefreshToken N)
- 최초 가입 닉네임은 구글/카카오 프로필 값 그대로. 이메일은 저장 (이메일 알림 기능 여부는 미결정).
- 회원 구분 USER / ADMIN, 회원 상태 ACTIVE / SUSPENDED
- User–SocialAccount는 1:N (향후 한 계정에 여러 소셜 계정 연결 확장 고려)

## 서비스 정책 요약
- 작품은 관리자가 등록, 회차 단위로 연재. 비회원은 각 작품 1화(프롤로그)까지만 감상, 2화부터 로그인 필요.
- 로그인 회원은 무료 공개 회차를 자유롭게 감상, 미공개 미리보기 회차는 잉크(INK)로 구매.
- 잉크: 플랫폼 전용 가상 재화(초기엔 실제 결제 없음). 미리보기 회차당 2잉크, 제작 완료된 회차만,
  초기 기준 최대 2회차까지, 구매한 회차는 재시청해도 추가 차감 없음, 무료 공개일이 되면 자동으로 무료 전환.
- 차별화 기능: 세계관 탐험(도감/지도/사건 기록), 순차 단서 수집(1화부터 순서대로 읽어야 획득, 건너뛰면 중단되고
  순서대로 다시 읽으면 재개, 완수 보상 없음), 선택형 특별 에피소드(분기 스토리), 독자 추리·예측.

## 개발 로드맵
| Phase | 범위 |
| --- | --- |
| 1 | 회원/소셜 로그인, User·SocialAccount, 작품·회차 관리, 웹툰 이미지 업로드, 뷰어, 비회원·회원 접근 권한 |
| 2 | 잉크 잔액, 사용/구매 내역, 미리보기 해금·개수 제한, 예약 무료 공개, 트랜잭션·동시성 처리 |
| 3 | 세계관 도감, 등장인물·지도, 독서 진행도, 순차 단서 획득, 스포일러 방지 |
| 4 | 선택지, 분기형 스토리, 사용자 선택 기록 |
| 5 | 독자 예측, 투표, 추리 게시판, 결과 공개 |

장기적으로 다른 작가의 작품 등록과 실제 결제·정산 기능을 추가한다.

## 현재 상태 / 다음 할 일 (2026-11-06 기준)
- 완료: User/SocialAccount/RefreshToken 엔티티, V1·V2 마이그레이션, Repository 3개, AuthService(+단위 테스트),
  임시 dev 로그인 AuthController(+웹 계층 테스트)
- 진행 중: AuthControllerTest 코드를 AI가 설명 → 사용자가 직접 설명해보며 이해도 확인
- 이후: AuthService 설계 리뷰(구글/카카오 code 교환, JWT 발급/검증, Refresh Token Rotation),
  Spring Security JWT 필터 설계, Webtoon·Episode 엔티티, 이미지 업로드, 뷰어, 접근 권한
- 임시 dev 로그인 엔드포인트는 실제 OAuth 연동 시 제거/교체한다.

## 코드 구조
- 패키지: `com.mytoon.backend.user.{controller, controller.dto, service, entity, repository}` (계층별 분리)
- DB 마이그레이션: `src/main/resources/db/migration` (Flyway)
