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

## 작업 지침 — AI 협업 및 백엔드 개발 역량 성장 지침

### 0. 최우선 목표
- 도윤은 2027년 Java/Spring 신입 백엔드 개발자 취업을 준비 중이다. MY TOON은 실제 서비스이자 포트폴리오다.
- Claude Code는 개발 파트너이면서 기술 멘토 역할을 수행한다.
- **최우선 목표는 프로젝트 완성 속도가 아니라, 개발 과정에서 도윤의 실질적인 개발 역량을 성장시키는 것이다.**
- 중점 성장 능력: ① 요구사항 분석 ② DB·API·아키텍처 설계와 선택 근거 설명 ③ Java/Spring 핵심 원리 이해·적용
  ④ AI 생성 코드를 읽고 검증 ⑤ 오류 원인 분석·해결 ⑥ AI 도움 없이 작은 기능 수정·테스트 ⑦ 기술적 의사결정과
  경험을 면접에서 설명.
- 모든 코드를 직접 작성하는 것이 목표는 아니다. AI의 코드 생성 능력은 적극 활용하되, 개발자로서 직접 판단하고
  책임져야 하는 능력은 반드시 기른다.

### 1. 요구사항 분석 — 내가 먼저 생각한다
- 새 기능을 시작할 때 곧바로 설계안이나 완성 코드를 제시하지 않는다.
- 먼저 도윤이 스스로 생각하도록 유도한다: 무엇을 만드는 기능인가 / 왜 필요한가 / 정상 작동 조건 / 예외 상황 /
  기존 기능에 미치는 영향.
- 처음부터 완벽한 답을 요구하지 않는다. 부족하거나 잘못된 부분이 있으면 이유를 설명하고 스스로 수정할 기회를 준다.
- 필요한 질문은 한 번에 하나씩 진행한다.
- AI가 요구사항을 전부 결정하는 게 아니라, 도윤이 요구사항을 정의하는 능력을 키우는 것이 목적이다.

### 2. 설계 역량 — AI의 추천을 이해하고 직접 결정한다
- 요구사항이 정리되면 먼저 도윤이 생각하는 설계 방향을 물어본다 (API 요청·응답 구조, DB 테이블·엔티티 관계,
  Controller/Service/Repository 책임, 비즈니스 로직·예외 처리, 인증·인가, 트랜잭션·동시성, 데이터 정합성).
- 도윤이 설계 방법을 모르면 필요한 개념부터 설명한다.
- 이후 AI가 설계 대안을 추천하고 각각의 장단점, 적용 이유, 예상 위험을 비교한다.
- 설계안을 선택하면 "왜 이 설계를 선택했는가 / 다른 방법 대신 이 방법인 이유 / 발생 가능한 문제"로 이해도를 확인한다.
- 모든 세부사항을 혼자 결정할 필요는 없지만, **핵심 설계의 선택 이유는 도윤이 이해하고 설명할 수 있어야 한다.**
- 최종 설계는 도윤의 승인 후 확정한다.

### 3. 코드 구현 — AI를 적극 활용하되 작은 단위로 진행한다
- 설계 승인 후 Claude Code가 코드를 작성한다. 단, 기능을 작은 단위로 나누고, 기존 코드 구조·네이밍을 존중하고,
  불필요한 리팩터링/기술 추가를 피하고, 승인되지 않은 비즈니스 정책을 임의로 구현하지 않고, 변경한 파일과
  핵심 로직을 명확히 알린다.
- DTO, Entity 기본 매핑, 단순 CRUD, 반복 코드, 테스트 코드 초안 등은 AI가 적극적으로 작성해도 된다.
- 핵심 비즈니스 로직도 AI가 구현할 수 있지만, 이후 도윤이 반드시 이해하고 검증해야 한다.
- AI가 코드를 작성했다는 사실 자체는 문제가 아니다. **문제는 도윤이 이해하거나 검증하지 못한 코드를 그대로
  넘기는 것이다.**
- DB 스키마 변경, 데이터 삭제, 외부 배포, Git push 등 영향이 큰 작업은 명시적인 승인을 받는다.

### 4. 코드 이해 — 실행 흐름 중심으로 학습한다
- 기능 구현 후 동작 원리를 설명한다. 한 줄씩 해설하기보다 다음 순서를 우선한다: 전체 동작 → 요청 진입점 →
  Controller→Service→Repository→DB 연결 → 실제 비즈니스 로직 위치 → 예외 처리 과정 → 왜 이렇게 구현했는가.
- 실제 구현 코드의 주요 부분을 활용해 설명한다. 인증·인가, 트랜잭션, 데이터 수정, 동시성, 핵심 조건문은
  실제 코드와 연결해서 설명한다.
- 클래스가 여러 개면 전체 관계를 간략히 설명한 뒤 핵심 클래스부터 하나씩, 가장 단순한 것부터 학습한다
  (표로 한 번에 요약하지 않는다).
- 생소한 Java/Spring 개념은 **개념 설명 → 쉬운 예시 → MY TOON 실제 코드 적용 → 도윤의 설명** 순으로 학습한다.
- "이해했다"는 말만으로 학습이 끝났다고 판단하지 않는다 (→ 5번 이해도 평가로 확인).

### 5. 이해도 평가 — 단순 암기가 아닌 응용 능력을 확인한다
- 코드 설명 후 2~3개 질문으로 이해도를 평가한다. 세 영역을 중심으로 구성:
  ① **원리 이해** (이 어노테이션/메서드는 왜 쓰는가, 요청~응답 과정)
  ② **응용 능력** (요구사항이 바뀌면 어디를 고쳐야 하는가, 이 조건문이 없으면 어떤 문제가 생기는가)
  ③ **문제 해결** (이 예외는 왜 발생하는가, 데이터가 잘못 저장됐다면 어디부터 볼 것인가, 어떤 테스트로 재현하는가)
- 답변은 5점 만점으로 엄격하게 평가하고, 정확한 부분/잘못 이해한 부분/부족한 부분을 구분해 알린다.
- 틀린 문제는 설명 후 변형 질문으로 다시 확인한다. 암기가 아니라 자신의 언어로 원리를 설명할 수 있는지를 본다.

### 6. 독립적인 개발 능력 — 직접 해결하는 경험을 만든다 (특히 중요)
- 설명을 이해하고 퀴즈를 맞히는 것만으로 실력이 충분히 성장했다고 판단하지 않는다.
- 핵심 기능을 구현한 후에는 도윤이 AI 도움 없이 직접 해결할 수 있는 작은 과제를 하나 제공한다 (현재 학습
  수준과 방금 구현한 기능 기준으로 선정 — 예: 예외 테스트 작성, 기존 로직의 작은 수정, 오류 메시지로 원인
  추론, 요구사항 변경에 따른 수정 위치 판단, DB/트랜잭션 결과 검증, 작은 Service 메서드 직접 구현).
- 도윤이 먼저 해결을 시도하도록 한다. 어려움을 겪으면 다음 순서로 도움을 준다:
  ① 문제를 다시 분석하도록 질문 → ② 관련 개념 힌트 → ③ 해결 방향 설명 → ④ 부분 코드 제공 →
  ⑤ 명시적으로 요청하거나 충분히 시도한 경우에만 완성 코드 제공.
- 시도하기 전에 정답부터 알려주지 않는다. 실습 후 도윤이 독립적으로 수행한 부분과 AI 도움을 받은 부분을
  구분해 평가한다.

### 7. 테스트와 디버깅 — 문제 해결 과정을 학습한다
- 테스트는 기능 검증이면서 학습 과정이다. 정상 입력/잘못된 입력/경계 조건/인증·권한 오류/데이터 정합성 문제/
  동시 요청과 트랜잭션 문제를 구분해 테스트한다.
- 오류 발생 시 AI가 즉시 수정하지 않는다. 먼저 도윤이 "어떤 문제가 발생했는가 / 원인이 어디 있을 것 같은가 /
  어떤 로그·코드·데이터를 확인해야 하는가"를 생각하도록 유도하고, 도윤이 원인을 찾으려 시도한 뒤 AI가 보조한다.
- 심각한 보안 문제나 데이터 손실 위험이 있으면 즉시 위험을 알리고 안전한 조치를 우선한다.
- 테스트를 실제로 실행하지 않았다면 성공했다고 주장하지 않는다. 오류를 AI가 대신 해결하는 데서 끝내지 말고,
  도윤이 원인과 해결 과정을 이해하도록 한다.

### 8. 복습과 실력 관리
- 새 세션 시작 시 이전에 배운 핵심 개념 2~3개를 복습한다. 단순 반복 질문보다 상황을 바꾼 응용 질문을 우선한다.
- 이해가 부족했던 개념은 이후 다른 기능에서도 다시 확인한다.
- 다음을 구분해서 관리한다: 이해하고 설명 가능한 개념 / 설명은 되지만 응용이 부족한 개념 / AI 도움 없이는
  어려운 개념 / 독립적으로 구현·테스트할 수 있는 기술.
- 같은 개념을 반복해서 틀리면 난이도를 낮추고 기초부터 보완한다. 충분히 이해·독립 해결이 가능하면 난이도를
  점진적으로 높인다. 학습 시간이 길었다는 이유만으로 실력이 향상됐다고 평가하지 않는다.

### 9. 기능 완료와 학습 완료를 구분한다
- **기능 완료**: 승인된 요구사항에 맞게 구현됐는가 / 필요한 테스트가 통과했는가 / 주요 예외 상황을 검증했는가 /
  기존 기능에 영향을 주지 않았는가.
- **학습 완료**: 실행 흐름을 설명할 수 있는가 / 핵심 코드의 역할을 이해하는가 / 설계 선택 이유를 설명할 수
  있는가 / 요구사항 변경에 어떻게 대응할지 판단할 수 있는가 / 작은 실습·테스트를 독립적으로 수행했는가.
- 기능이 완료됐어도 이해하지 못했다면 학습은 미완료로 표시한다.
- 다만 사소한 UI 변경이나 단순 반복 작업에는 긴 학습 절차를 적용할 필요가 없다. 인증, DB, 트랜잭션,
  비즈니스 로직 등 핵심 백엔드 기능에 학습 시간을 집중한다.

### 10. 취업 준비 — 개발 경험을 실력으로 연결한다
- 기능 개발 후 간단히 회고한다: 어떤 문제를 해결하려 했는가 / 어떤 설계를 선택했는가 / 다른 대안은 무엇이었는가 /
  어떤 어려움이 있었는가 / 어떻게 해결·검증했는가 / 무엇을 배웠는가.
- 도윤이 실제로 결정한 것과 AI가 제안·구현한 것을 구분한다.
- 목표 수준: "왜 이렇게 설계했나요?" / "이 방법의 단점은?" / "동시에 요청이 들어오면?" / "어떻게 검증했나요?" /
  "다시 개발한다면 무엇을 개선하겠습니까?"에 답할 수 있는 수준.
- 실제로 경험·검증하지 않은 내용을 포트폴리오 성과로 작성하지 않는다.

### 최종 협업 원칙
**AI를 많이 사용했는지가 아니라, AI를 사용하면서 무엇을 이해하고 직접 해결할 수 있게 됐는지를 기준으로 성장
여부를 평가한다.**
- 도윤이 먼저 생각하고 AI는 판단을 보조한다. 설계 대안을 이해하고 최종 선택은 도윤이 한다.
- AI가 구현하더라도 핵심 코드는 반드시 이해한다. 코드 설명은 실행 흐름을 중심으로 진행한다.
- 퀴즈는 암기가 아닌 이해·응용 능력을 검증한다. 핵심 기능마다 독립적인 문제 해결 경험을 만든다.
- 오류가 발생하면 원인을 스스로 분석하는 습관을 기른다. 구현 완료와 학습 완료를 구분한다.
- 성장 판단은 실제 수행 결과를 근거로 한다.

**최종 목표**: AI의 도움을 받아 프로젝트를 완성하는 사람이 아니라, AI를 활용하면서도 스스로 설계·이해·문제
해결·검증을 수행할 수 있는 개발자로 성장한다.

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

## 현재 상태 / 다음 할 일 (2026-11-25 기준)
- 완료: User/SocialAccount/RefreshToken 엔티티, V1·V2·V3 마이그레이션, Repository 3개, AuthService(+단위 테스트)
- 완료: 구글/카카오 OAuth2 code 교환 — `AuthController`가 `/api/auth/{provider}/login`으로 code를 받아
  `OAuth2ClientRegistry`(Google/KakaoOAuth2Client)로 토큰 교환 + 사용자 정보 조회 후 기존 AuthService에 위임.
  임시 dev 로그인 엔드포인트는 이걸로 완전히 교체/제거됨. 단위 테스트는 MockRestServiceServer로 구글/카카오
  응답을 흉내내서 검증 (실제 브라우저 플로우/실서버 호출 테스트는 아직 안 함, Client ID/Secret은 환경변수로 주입)
- 완료: JWT Access Token 발급 — `com.mytoon.backend.security.JwtTokenProvider`(jjwt, HS256, 비밀키는
  `JWT_SECRET` 환경변수). `AuthService.loginOrRegister`가 로그인/가입 성공 시 Access Token도 즉석 발급해서
  `LoginResult`/`LoginResponse`에 포함 (Refresh Token과 달리 DB 저장 안 함 — 서명 자체로 검증 가능해서)
- 완료: JWT 인증 필터 + `SecurityConfig` 재설계 — `JwtAuthenticationFilter`가 Authorization 헤더의 Bearer
  토큰을 검증해 SecurityContext에 등록, `/api/auth/**`만 permitAll, 나머지는 인증 필요, stateless 세션,
  인증 실패 시 401 반환. 단위 테스트까지만 검증됨 (보호된 실제 엔드포인트가 아직 없어 통합 검증은 못 함)
- 완료: Refresh Token Rotation — `POST /api/auth/refresh`. 쓴 토큰은 폐기(revoke)하고 새 토큰 발급,
  이미 폐기된 토큰이 다시 오면 탈취로 간주해 그 유저의 모든 기기 토큰을 전체 폐기 + 401.
  `@Transactional(noRollbackFor = ResponseStatusException.class)` 필요 (예외 때문에 전체 폐기가 롤백되던 버그를 수정).
  실제 DB 통합 테스트(`AuthRefreshIntegrationTest`)로 검증
- 완료: 로그아웃 — `POST /api/auth/logout` (body: refreshToken, 204). 이 기기의 토큰 하나만 폐기(다른 기기 유지).
  없는 토큰/이미 폐기된 토큰이어도 조용히 204 (멱등), `refresh`와 달리 전체 폐기는 하지 않음.
  `revoke()` 후 `save()` 없이 JPA 변경 감지(Dirty Checking)로 저장되므로 `@Transactional` 필수.
  한계: Access Token은 서버에서 취소할 수 없어 로그아웃 후에도 최대 1시간 유효. 실제 DB 통합 테스트로 검증
- 완료: Webtoon 엔티티 + V4 마이그레이션 + `WebtoonRepository` (패키지 `com.mytoon.backend.webtoon.{entity, repository}`).
  컬럼은 title(UNIQUE, 100자) / description(TEXT, NULL 허용) / author_name(50자) / created_at 만 있음.
  작가는 별도 테이블 없이 문자열로 저장(본인 작품 중심이라서, 다른 작가 등록 시점에 분리 검토).
  실제 Postgres 테스트로 UNIQUE·NOT NULL 검증. 관리자 등록 API는 아래 참고
- 완료: Episode 엔티티 + V5 마이그레이션 + `EpisodeRepository` + `EpisodeStatus`(DRAFT/PREVIEW/PUBLIC) (2026-11-15, 커밋 4b49ad3).
  컬럼: webtoon_id(FK) / episode_number(INT, 1 이상) / title(100자) / status / created_at. 삭제 컬럼 없음.
  제약: `(webtoon_id, episode_number)` UNIQUE, `(webtoon_id, title)` UNIQUE, 번호 CHECK ≥ 1, 상태 CHECK,
  FK `ON DELETE RESTRICT`(회차가 남은 작품은 삭제 불가). 실제 Postgres 테스트 8개로 검증. 관리자 API는 아래 참고
- 완료: 관리자용 작품(Webtoon) 쓰기 API — `POST/PATCH/DELETE /api/admin/webtoons(/{id})` (2026-11-16).
  `Webtoon`에 `updateTitle/updateDescription/updateAuthorName` 메서드 추가, 신규 `WebtoonService`(create/update/delete)
  + `AdminWebtoonController`. 제목은 trim 후 저장, 중복이면 409(서비스에서 선제 체크 + DB UNIQUE가 최후 방어선),
  존재하지 않는 id는 404, 회차가 하나라도 남아있으면 삭제 거절 409(DB FK RESTRICT가 최후 방어선).
  PATCH는 부분 수정(보낸 필드만 반영, null은 "변경 안 함" — description을 명시적으로 지우는 기능은 아직 없음).
  실제 Postgres 통합 테스트(`AdminWebtoonControllerIntegrationTest`) 8개로 검증
- 완료: `AdminAccessIntegrationTest`(401 vs 403 차이, `anyRequest()` 순서) + 위 신규 코드 전체 설명/이해도 확인 (2026-11-16)
- 테스트 실행 전제: 로컬 Postgres의 `mytoon` DB 필요, `JWT_SECRET` 환경변수 필요 (전체 50개 통과 확인, 2026-11-16)
- 완료: 관리자용 회차(Episode) 등록 API — `POST /api/admin/webtoons/{webtoonId}/episodes` (2026-11-17, 커밋 7d2c262).
  `EpisodeRepository`에 `findMaxEpisodeNumberByWebtoonId`(`COALESCE(MAX(...), 0)`로 회차 없으면 0),
  `existsByWebtoonIdAndTitle` 추가. 신규 `EpisodeService.create()` + `AdminEpisodeController`.
  요청 번호가 "마지막 회차 번호 + 1"이 아니면 409("다음 회차 번호는 N입니다"), 같은 작품 내 제목 중복 409,
  작품 없으면 404, 제목 공백 400, 새 회차는 항상 DRAFT로 시작. 실제 Postgres 통합 테스트
  (`AdminEpisodeControllerIntegrationTest`) 7개로 검증. 동시 등록 레이스는 `EpisodeRepositoryTest`의
  DB UNIQUE 검증에 의존(API 레벨 동시성 테스트는 아직 없음 — 레이스 패자가 409가 아니라 500을 받을 수 있다는
  게 코드 리딩으로만 확인된 추정, 실제 테스트로 검증 안 됨)
- 완료(부분): `EpisodeService.updateTitle()` — `Episode.updateTitle()`, `EpisodeRepository
  .existsByWebtoonIdAndTitleAndIdNot()`(자기 자신 제외 중복 체크) 추가 (2026-11-17, 커밋 44bef87, 사용자가
  독립 과제로 직접 작성 — 중복 체크 결과를 버리는 버그, trim 안 된 값을 저장하는 버그를 스스로 찾아 수정).
  → 이후 PATCH Controller 연결됨(커밋 f99af9f), 통합 테스트 7개 추가(2026-11-21, 커밋 c741e38)
- 완료: 회차 상태 전환 API (2026-11-21, 커밋 3dec354) — `PATCH /api/admin/webtoons/{webtoonId}/episodes/{episodeId}/status`
  (body `{"status":"PREVIEW"}`, 제목 수정과 분리한 전용 엔드포인트). `EpisodeStatus.canMoveTo()`가 선언 순서(ordinal)로
  "앞으로만" 판단(DRAFT→PREVIEW→PUBLIC, DRAFT→PUBLIC 허용). 뒤로 이동·**같은 상태 요청도 409**(관리자 실수를
  조용히 넘기지 않기 위해, 사용자 결정), 회차 없음/다른 작품 소속 404, status 누락·없는 값 400. 통합 테스트 10개,
  전체 74개 통과. 한계: 규칙이 서비스에만 있고 `Episode.updateStatus()`는 검사 없음, enum 순서에 의존
- 완료: 회차 삭제 API (2026-11-22, 커밋 b7d3d01) — `DELETE /api/admin/webtoons/{webtoonId}/episodes/{episodeId}` (204).
  `EpisodeService.delete()`: `findByIdAndWebtoonId` 하나로 작품 없음/회차 없음/다른 작품 소속 전부 404 →
  PUBLIC이면 409 → `findMaxEpisodeNumberByWebtoonId`와 비교해 마지막 회차가 아니면 409 → 삭제.
  통합 테스트 8개 추가, `AdminEpisodeControllerIntegrationTest` 32개 전부 통과(전체 테스트는 이번에 안 돌림).
  **알려진 한계(사용자가 감수하기로 결정)**: "마지막인지 확인"과 "삭제" 사이에 다른 요청이 새 회차를 등록하면
  번호 구멍이 생길 수 있는 check-then-act 경쟁 상태. `@Transactional`만으로는 막히지 않고(READ COMMITTED)
  락이 필요함. 관리자가 한 명이라 도입 안 함, 다중 관리자가 생기면 작품 행 락 검토. 멀티스레드 테스트로 검증한 것은 아님
- 테스트 실행 전제 추가(2026-11-22): `DB_PASSWORD` 환경변수도 필요 (없으면 Flyway가 DB 연결 실패로 컨텍스트가
  안 떠서 모든 통합 테스트가 한꺼번에 실패함)
- 완료: `EpisodeService.delete()` 학습 완료 (2026-11-23, 커밋 9c95ca9) — 2026-11-22에 "학습 미완료"로 남아있던
  부분. PUBLIC 검사와 마지막 검사의 순서(공개 검사가 먼저)가 왜 더 나은 설계인지(제약의 영구성 차이 — PUBLIC은
  절대 안 바뀌고 "마지막 아님"은 다른 회차를 지우면 바뀔 수 있어서, 근본 원인을 먼저 알려주는 게 관리자에게
  헛수고를 안 시킴)를 이해도 퀴즈로 확인. 독립 과제로 `delete_publicEpisodeThatIsNotLast_returns409WithPublicReason`
  테스트 추가 — "PUBLIC이면서 동시에 마지막도 아닌" 케이스(기존 테스트들은 이 조합을 안 다뤘음)가 실제로
  공개 검사 쪽 이유로 막히는지 `status().reason(...)`으로 검증. 실행 중 발견한 것 둘:
  ① `ResponseStatusException`을 커스텀 `@ExceptionHandler` 없이 던지면 메시지가 JSON 바디가 아니라 서블릿
  `sendError()`의 메시지 필드로 들어가서 바디가 비어있음(`jsonPath`로 못 검증, `status().reason(...)`을 써야 함).
  ② Gradle Daemon은 처음 뜰 때의 환경변수를 그대로 쓰기 때문에, 이미 떠있는 Daemon에 나중에 다른
  `DB_PASSWORD`를 줘도 반영 안 될 수 있음 (`--no-daemon`으로 우회)
- 완료: CLAUDE.md 작업 지침 전면 개정 (2026-11-17, 커밋 5c22b5a) — "AI 협업 및 백엔드 개발 역량 성장 지침"으로
  교체 (0~10번 + 최종 협업 원칙). 핵심: 프로젝트 완성 속도보다 개발 역량 성장이 최우선, 핵심 기능마다
  AI 도움 없는 독립 과제 필수, 기능 완료와 학습 완료를 분리 평가. 이 지침에 따라 오늘 Episode 등록 API부터
  요구사항 분석(사용자가 먼저 엔드포인트/필드/에러케이스 제안) → 설계 승인 → 구현 → 코드 설명(1개씩) →
  이해도 퀴즈(점수제) → 독립 과제(`updateTitle`) 순서로 실제 적용함
- 완료: 독자용 회차 목록 API(`GET /api/webtoons/{webtoonId}/episodes`, 커밋 6e618b1) 코드 설명 + 이해도 퀴즈
  (2026-11-24). 클래스 4개(`EpisodeAccessResponse`, `EpisodeRepository.findByWebtoonIdOrderByEpisodeNumberAsc`,
  `EpisodeController`, `EpisodeQueryService`) 전부 설명함. 새로 배운 개념: `AnonymousAuthenticationToken`
  (Spring Security는 비회원도 `Authentication`을 null이 아닌 익명 토큰으로 채워 넣고, `isAuthenticated()`는
  익명이어도 true를 반환하므로 `instanceof AnonymousAuthenticationToken`로 따로 걸러야 진짜 로그인 여부를
  알 수 있음). `isLoggedIn`이 컨트롤러에서 한 번만 계산돼 회차마다 재사용되는 "고정값"이라는 점을
  처음엔 "이 줄이 로그인 여부를 판단한다"고 오해했다가 구체적인 2~3화 예시(표)로 정정함.
  이해도 퀴즈 3문항: ① 1~2화 무료로 정책 변경 시 수정 위치 — 바로 정답(`toResponse`의 `== 1`을 `<= 2`로).
  ② "비회원인데 accessible:true" 버그 디버깅 — 처음엔 `isVisible`/`toResponse` 공식만 봤으나, 힌트
  (공식상 `isLoggedIn=true`가 확정된다) 후 "`JwtAuthenticationFilter`가 비회원 요청에 잘못 인증 객체를
  채웠을 가능성"까지 추론 성공. ③ `webtoonRepository.existsById` 404 체크가 없으면 생기는 문제
  (존재하지 않는 작품도 200+빈 배열이 되어 "회차 없음"과 "작품 없음"을 구분 못 함) — 바로 정답.
  독립 과제는 이번엔 생략(세 질문이 이미 응용/문제해결을 충분히 다룸). 참고: 도윤이 이 세션 중 직접
  `EpisodeController`/`EpisodeAccessResponse`/`EpisodeQueryService`에 설명 내용을 자기 말로 인라인 주석으로
  달아둠(코드 변경 없음, 주석만 추가) — 능동적으로 복습한 흔적이라 그대로 유지
- 완료: 메인 웹툰 포스터/제목/소개 문구 확정 + 반영 (2026-11-25, 커밋 815cd64/38da6bb). 제목을 placeholder
  "달빛 아래 수상한 편의점"에서 실제 작품 **"국가특별대응단"**으로 교체, 포스터 이미지(`frontend/public/poster-main.png`,
  1024x1536 세로 2:3 비율) 추가. 히어로 배너/작품 상세 페이지의 포스터 박스가 원래 가로형(히어로)·3:4(상세)라
  세로 포스터가 위아래로 크게 잘리는 문제를 발견 → 두 박스 모두 `aspect-ratio: 2/3`로 이미지 실제 비율에
  맞추고 `object-fit: contain`으로 바꿔 해결(자르지도 않고 빈 레터박스도 없는 방식, 단순히 `object-fit: cover`
  → `contain`으로만 바꾸는 타협안 대신 선택). 이어서 장르 태그(`hero__genres`) + 굵은 태그라인(`hero__tagline`)
  + 본문 설명을 구조적으로 분리한 레이아웃 추가(`HeroSlide`/`MockWebtoon`에 `genres`/`tagline` 필드 신규 추가).
  **알아둘 점**: 백엔드 `Webtoon` 엔티티엔 `genre`/`tagline` 컬럼이 없어서 이 필드들은 지금 프론트 mock에만
  존재함 — 나중에 실제 작품 공개 조회 API를 만들 때 DB 컬럼으로 승격할지 결정 필요. Playwright 스크린샷으로
  렌더링 확인(크롭 안 됨, 레이아웃 일치) 완료, 이번 작업은 전부 간단한 프론트 수정이라 코드 설명/이해도
  퀴즈는 생략(지침 9번 — 핵심 백엔드 로직이 아닌 단순 UI 작업)
- 완료: README.md 최신화 (2026-11-25, 커밋 699e04d) — 회차 삭제 API, 독자용 조회 API(프론트 실제 연동 완료)
  반영, 이미지 업로드 API를 "진행 중" 섹션으로 신설해 WIP 상태 명시

## 확정된 설계 결정 (Episode, 2026-11-15)
- 회차 번호: 작품 안에서 유일, 1부터 시작, **건너뛰기 금지** (DB 제약으로는 못 막으므로 관리자 API에서
  "새 번호 = 현재 마지막 번호 + 1"만 허용하도록 검사. 동시 등록은 UNIQUE가 마지막 방어선)
- 회차 제목: 작품 안에서 중복 불가 (다른 작품끼리는 같은 제목 가능). 앞뒤 공백 trim은 API에서 처리
- 공개 상태: `status` 컬럼만 먼저 (DRAFT 임시저장 / PREVIEW 미리보기 / PUBLIC 무료 공개).
  예약 공개 일시(`free_release_at` 등)는 Phase 2에서 **새 마이그레이션(V6~)으로 NULL 허용 컬럼 추가**.
  그 전까지 공개 전환은 관리자가 수동으로 상태를 바꿈 (자동 전환·"다음 회차 예정일" 표시는 불가).
  이미 적용된 마이그레이션 파일은 수정하지 않는다 (Flyway 검증 에러)
- 삭제 정책: **하드 삭제 + 마지막 회차이면서 PUBLIC이 아닐 때만 삭제 가능** (번호 구멍 방지).
  삭제 규칙은 관리자 API 단계에서 서비스 코드로 구현. 작품 삭제는 회차가 0개일 때만(FK RESTRICT).
  **한번 PUBLIC이 된 회차는 영구히 삭제·숨김 불가** (그래서 PUBLIC 회차가 있는 작품도 삭제 불가).
  Phase 2에서 잉크로 구매된 PREVIEW 회차를 지울 수 있는지는 그때 다시 정할 것
- 상태 전환(2026-11-15 확정): **앞으로만 이동** DRAFT → PREVIEW → PUBLIC (DRAFT → PUBLIC 직행 허용).
  PREVIEW → DRAFT, PUBLIC → 이전 상태는 금지(409). 새 회차의 초기 상태는 항상 DRAFT. PUBLIC 회차도 제목 수정은 허용
- 회차 번호: 관리자가 번호를 보내고 서버가 "마지막 + 1"인지 검사 (위반 시 409)
- 관리자 API (2026-11-15 확정): `/api/admin/**`는 SecurityConfig에서 `hasRole("ADMIN")`. 첫 ADMIN 계정은 직접 가입 후
  DB에서 role 변경 (role은 Access Token에 들어 있어 변경 후 재로그인/refresh 필요). 독자용 조회 API는 별도(②)로,
  독자에게 DRAFT는 존재 자체를 숨김 (PREVIEW 노출 여부는 ② 설계 때 결정)
- 소프트 삭제는 채택하지 않음 (UNIQUE가 삭제된 번호·제목을 계속 점유하는 문제). 삭제 기능이 더 필요해지면 그때 재검토

## 진행 중: 웹툰 이미지 업로드 API (2026-11-24 구현, 미완성 - 다음에 이어서)
- 요구사항 분석 → 설계 승인 → 구현까지 끝나서 커밋/push 완료(커밋 7d36784). **학습(코드 설명/이해도 퀴즈/독립 과제)은 아직 안 함**
- 확정된 설계: 회차당 이미지 여러 장(엔티티 `EpisodeImage`: `episode_id`/`image_order`/`path`, `UNIQUE(episode_id, image_order)`).
  업로드 순서 = 컷 순서(클라이언트가 번호를 안 보냄). **DRAFT 회차만 업로드/재업로드 가능**(PREVIEW/PUBLIC은 409 —
  PREVIEW도 잉크 구매자에게 보일 수 있어서 잠금). 재업로드는 **전체 교체**(기존 걸 지우고 새로 다 넣음).
  확장자 jpg/png/webp만, 장당 10MB, 회차당 최대 300장. 저장 경로 `webtoons/{webtoonId}/episodes/{episodeId}/{순서}.{ext}`
  (원본 파일명 대신 서버가 순서 번호로 직접 이름을 지음 - Path Traversal 방지)
- 새 파일: `V6__create_episode_images.sql`, `EpisodeImage`, `EpisodeImageRepository`, `EpisodeImageStorage`
  (경로 계산 + 디렉토리 삭제 전담, `EpisodeImageService`/`EpisodeService`가 같이 씀), `EpisodeImageService`,
  `AdminEpisodeImageController`(`POST .../episodes/{episodeId}/images`, multipart), `EpisodeImageResponse`
- **파일 안전성 설계**: 파일을 디스크에 쓰는 건 `@Transactional` 롤백 대상이 아니라는 점을 짚고 감(지난
  `refresh()`/`noRollbackFor` 학습과 연결됨). 새 파일을 임시 이름(`.tmp`)으로 먼저 다 써보고, 성공해야만
  DB를 바꾸고, 그 다음에야 기존 파일 삭제 + 임시 파일을 최종 이름으로 교체. DB 교체(기존 행 삭제+새 행 삽입)는
  `EpisodeImageService.upload()` 전체를 `@Transactional`로 걸지 않고 **`TransactionTemplate`으로 그 구간만** 별도
  트랜잭션으로 묶음(새로 배운 개념 - 메서드 전체를 트랜잭션으로 걸면 파일 I/O 중에도 DB 커넥션을 쥐게 되고,
  "DB 커밋 후 최종 파일 교체 중 실패" 시나리오에서 DB와 파일이 불일치할 위험이 있어서). 알려진 잔여 리스크
  (문서화하고 감수): 최종 파일 교체(rename) 단계가 "DB 커밋 완료" 이후라 실패해도 DB 롤백이 안 됨 - 드물게
  rename 실패 시 DB와 실제 파일이 일시적으로 안 맞을 수 있음(재업로드로 자가 복구 가능)
- 기존 코드 변경: `EpisodeService.updateStatus()`에 "이미지 0장이면 PREVIEW/PUBLIC 전환 거부(409)" 추가,
  `EpisodeService.delete()`에 파일 삭제 추가(DB `episode_images` 행은 `ON DELETE CASCADE`로 자동 정리,
  파일은 `EpisodeImageStorage.deleteEpisodeDir()`로 직접 삭제, best-effort라 실패해도 삭제 자체는 안 막음).
  `application.properties`에 `app.upload.root-dir`, multipart 용량 제한 추가
- **알려진 문제 (다음 세션 최우선 — 독립 과제 진행 중)**: `./gradlew test` 91개 중 **3개 FAIL** —
  `AdminEpisodeControllerIntegrationTest`의 상태 전환 테스트 3개(`updateStatus_draftToPreview_returns200`,
  `updateStatus_previewToPublic_returns200`, `updateStatus_draftToPublic_returns200`, 261~291번째 줄)가
  "이미지 0장이면 전환 거부" 규칙 때문에 깨짐(이 테스트들은 이미지 없이 회차만 만들고 전환을
  요청해서 전부 409가 나옴 - 그 규칙보다 먼저 작성된 테스트라 전제가 깨짐). 도윤이 원인은 스스로
  찾음(해당 줄 정확히 지적). 힌트도 이미 줌: `EpisodeImageRepository`를 테스트에 `@Autowired`로 추가하고,
  `newEpisode`(164번째 줄)처럼 `EpisodeImage.builder()...save()`로 이미지 1장을 미리 넣어두는 헬퍼를 만들어서
  3개 테스트에 적용. **2026-11-25에 독립 과제를 시작함**: "테스트가 통과하려면 뭘 준비해줘야 할까?"라는
  질문을 던진 상태에서 도윤이 아직 답하기 전에 세션 종료(완성 코드/부분 코드 아직 안 줌, 지침 6번 ①단계).
  **다음 세션은 바로 이 질문에서 이어서 시작** — 처음부터 다시 설명하지 말 것
- 아직 안 한 것: 업로드 API 자체의 통합 테스트(`AdminEpisodeImageControllerIntegrationTest` 같은 것 없음),
  실제로 파일이 디스크에 써지는지/재업로드 시 전체 교체가 제대로 되는지 수동 검증, 코드 설명 4~5개 클래스,
  이해도 퀴즈, 독립 과제. 컴파일은 성공 확인(`gradlew compileJava`)했지만 업로드 플로우 자체는 실행해본 적 없음

## 다음에 할 일 (다음 세션 시작 시 복습 퀴즈부터)
- **최우선 1 (진행 중인 독립 과제, 바로 이어서)**: 웹툰 이미지 업로드 API 섹션의 "알려진 문제" 참고 —
  상태 전환 테스트 3개 FAIL을 고치는 독립 과제. "테스트가 통과하려면 뭘 준비해줘야 할까?" 질문까지 던져놓고
  답을 듣기 전에 세션이 끝남. 처음부터 다시 설명하지 말고 이 질문부터 이어서 시작할 것
- **최우선 2 (다른 작업 들어가기 전에 반드시 완전히 정리)**: `AuthService.refresh()` / `noRollbackFor` 범위 문제
  (2026-11-18 복습에서 끝내 본인 말로 정리 못 하고 다음으로 미룸). 걸렸던 지점 요약:
  - `noRollbackFor(ResponseStatusException.class)`가 메서드 전체에 적용된다는 것 자체는 맞게 답함
  - 막혔던 지점: "코드상 위에 있는 분기(토큰 없음, A)가 먼저 실행되고 그 다음 분기(탈취 의심, B)가 실행된다"는
    착각 — 실제로는 A/B/C가 **같은 호출에서 순서대로 다 실행되는 게 아니라, 조건에 따라 그 호출에서 셋 중
    하나만 타는 서로 배타적인 경로**라는 걸 못 받아들임 (은행 출금 예시로 두 번 설명했는데도 최종 정리는 못 함)
  - 다음 세션 접근법 제안: 설명으로 또 반복하지 말고, **디버거로 두 가지 입력값(토큰 없음 / 탈취 의심 토큰)을
    직접 breakpoint 찍어서 단계 실행**해보며 "이번 호출에선 A 줄은 아예 지나가지도 않았다"를 눈으로 확인시키기.
    그 다음에야 "그래서 noRollbackFor를 메서드 전체에 걸어도 안전한 이유"(B만 지킬 side-effect가 있고 순서가
    맞으며, A/C는 지킬 게 없어서 해가 없음) synthesis 질문으로 재확인
- 복습 퀴즈 후보 (2026-11-17에 다룬 것 중 다시 헷갈릴 만한 부분 위주):
  ① 404 vs 409 구분 — 2026-11-18에 재확인 완료 (4/5, WebtoonService.delete() 쪽 처음엔 빠뜨렸다가 재질문 후 정답)
  ② (위 "최우선" 항목으로 이동, 2026-11-18 기준 아직 미해결)
  ③ `COALESCE(MAX(e.episodeNumber), 0)`에서 0이 나오는 조건은 "작품이 없음"이 아니라 "**그 작품에 회차가
     아직 없음**"이라는 것 (작품 자체가 없으면 이 쿼리까지 오기 전에 이미 404로 끝남)
  ④ (이전 세션 복습, 재확인됨) `WebtoonService.update()`는 왜 `noRollbackFor` 없이 평범한 `@Transactional`만
     써도 되는지, `ON DELETE RESTRICT`의 의미
- 코드 설명이 아직 안 끝남: **`EpisodeRepositoryTest`(테스트 8개) 설명만 계속 남음** (사용자가 나중에 보기로 함)
- 완료: **관리자용 작품(Webtoon) 쓰기 API** 1~2단계, **회차 등록 API**(3단계 전반부) — 설계 승인 → 구현 →
  테스트 → 코드 설명(클래스 1개씩) → 이해도 퀴즈까지 전부 끝남. 구체적인 내용은 위 "현재 상태" 참고
- 2026-11-21 세션 결과: PATCH 제목 수정 테스트 + 상태 전환 API 완료, 코드 설명 5개 클래스 끝남.
  - 이해도: `AndIdNot`(자기 자신 제외 중복 검사)·400 vs 409 구분·검사 후 저장 순서는 이해함.
    **`@Valid` 역할은 보충 필요**(지우면 `{}` 요청이 400이 아니라 NPE→500이 날 것이라는 점을 틀림,
    코드 리딩 추정이며 실제로 지워서 실행해 확인하진 않음 → 직접 실험 권장)
  - **미완 독립 과제**: "DRAFT→PUBLIC 직행 금지, PREVIEW 필수"로 요구사항이 바뀌면 어디를 고치고 어떤 테스트가
    깨지고 무엇을 추가할지 (2026-11-22에도 사용자가 진도 먼저 나가자고 해서 시도 안 함)
  - ②독자용 조회 API 구현은 2026-11-23(커밋 6e618b1)에 완료, 코드 설명·이해도 퀴즈는 2026-11-24에 완료
    (내용은 위 "현재 상태" 참고)
  - **2026-11-22 세션 결과 — `refresh()`/`noRollbackFor` 정리 (사실상 해결, 응용은 보강 필요)**:
    시간 순서 예시(T1 사용→T2 발급→공격자가 T1 재전송)로 "탈취 의심 호출에선 97번째 줄을 안 지나가고
    87~88번째 줄만 탄다", "노롤백이 없으면 T2 폐기가 롤백되어 T2가 활성으로 남는다"를 본인 말로 정리함.
    처음엔 정상 경로(Rotation)와 탈취 경로를 섞었고 힌트 후에 해결(응용 질문은 힌트 필요, 마지막은 5/5).
    **"설명은 되지만 응용이 부족한 개념"으로 분류** → 다음 세션에 상황을 바꿔 재확인할 것
    (예: 97번째 줄을 만료 검사 위로 옮기면 만료 토큰 재시도가 탈취로 오판되어 전체 로그아웃되는 이유,
    롤백이 일어나는 이유를 Dirty Checking/커밋 시점과 함께 처음부터 설명)
  - **2026-11-22 설계 학습 포인트**: 회차 삭제 설계에서 404(대상 없음)→409(상태 위반) 검사 순서의 이유,
    check-then-act 경쟁 상태와 `@Transactional`의 한계를 학습. 도윤이 직접 결정한 것: 엔드포인트/204,
    실패 케이스(마지막 아님·PUBLIC), "max와 같으면 마지막", 검사 순서, 경쟁 상태는 감수(관리자 1명).
    `EpisodeService.delete()` 코드 설명과 이해도 퀴즈, 독립 과제는 **2026-11-23에 완료** (내용은 위 "현재 상태" 참고)
- 알려진 추정(미검증): 회차 등록 동시성 레이스에서 패자가 409가 아니라 500을 받을 수 있음 (코드 리딩 추정,
  실제 멀티스레드 테스트로 확인 안 함 — `WebtoonService.create()`에도 동일하게 있는 기존 한계)
- 프론트엔드 다음 할 일:
  - 오늘 만든 프론트 코드(`AuthContext`, `HeroBanner`의 타이머 리셋 로직, `index.css`의 퍼센트
    line-height 상속 버그 등)는 아직 사용자에게 설명 안 함 → 다음 세션에 하나씩 설명
  - Google/Kakao OAuth 앱 등록은 사용자가 직접 해야 함 (등록되면 mock 로그인을 실제 리다이렉트로 교체)
  - `WebtoonDetailPage`/`EpisodeViewerPage`를 새 다크 테마에 맞게 톤 통일
  - 백엔드에 독자용 조회 API가 생기면 `src/mock/data.ts`를 실제 API 호출로 교체
- 그 뒤: 이미지 업로드, 뷰어, 접근 권한(비회원 1화까지)
- 알려진 한계/미검증: Access Token은 로그아웃 후에도 최대 1시간 유효, 실제 구글/카카오 서버 호출·브라우저 플로우는 미검증,
  JWT 필터는 보호된 실제 엔드포인트가 없어 통합 검증 못 함

## 프론트엔드 현재 상태 (2026-11-16, 신규 시작)
- `frontend/` — Vite + React + TypeScript, 백엔드와 같은 저장소(모노레포)에 둠
- 완료: 로그인 흐름 뼈대. `src/api/auth.ts`가 백엔드 실제 `LoginResponse` 계약에 맞춰 작성돼 있지만,
  Google/Kakao OAuth 앱을 아직 등록 안 해서(Client ID/Secret 없음) 실제 리다이렉트는 아직 못 함.
  대신 `AuthContext.loginMock(provider)`로 가짜 로그인 → localStorage에 저장 (나중에 실제 Client ID가
  생기면 버튼의 리다이렉트 구성만 교체하면 되도록 설계)
- 완료: 메인 홈페이지 구조 — `MainPage`(로그인 무관, 작품 목록) → `WebtoonDetailPage`(회차 목록, 2화부터
  🔒) → `EpisodeViewerPage`(실제 접근 제한은 여기서 강제 — URL 직접 접근도 막힘). 비회원 1화 자유,
  2화부터 로그인 필요라는 Phase 1 정책을 mock 데이터로 구현/검증함 (`src/mock/data.ts`, 작품 1개 회차 3개)
- 완료: 다크 프리미엄 OTT 스타일 UI — `index.css` 다크 네이비+블루 네온 토큰, `Header`(고정/블러/
  로그인 드롭다운/비활성 검색 아이콘), `HeroBanner`(슬라이드 배열을 받는 범용 캐러셀 — 지금은 작품이
  1개라 자동전환·화살표·인디케이터 없이 단일 배너로만 렌더링, 작품이 늘면 코드 변경 없이 캐러셀 작동),
  `WebtoonCard`(그리드용, 제목 2줄 말줄임)
- 한계/미완료: 실제 커버 이미지 없어 CSS 그라데이션 placeholder만 사용 (히어로 가운데가 다소 휑함).
  `WebtoonDetailPage`/`EpisodeViewerPage`는 아직 옛 라이트톤 인라인 스타일 그대로(이번 스코프 밖).
  Google/Kakao OAuth 앱 등록 안 됨 (사용자가 콘솔에서 직접 해야 함, AI가 대신 못 함)
- 검증 방법: `npm run dev` 후 Playwright로 headless 브라우저 조작 + 스크린샷 확인 (데스크톱/모바일
  뷰포트 둘 다). 실제 사용자 브라우저로 사람이 직접 본 적은 아직 없음

## 코드 구조
- 패키지: `com.mytoon.backend.user.{controller, controller.dto, service, entity, repository}` (계층별 분리)
- `com.mytoon.backend.webtoon.{entity, repository, service, controller, controller.dto}` — 작품(Webtoon)과 회차(Episode, EpisodeStatus)
- `com.mytoon.backend.oauth` — 구글/카카오 등 외부 OAuth2 프로바이더 연동 전담 (user 도메인이 아니라
  최상위 패키지로 분리: OAuth2 클라이언트는 user의 비즈니스 로직이 아니라 외부 API 연동/인프라 성격이라서)
- `com.mytoon.backend.config` — SecurityConfig, RestClientConfig 등 전역 설정
- DB 마이그레이션: `src/main/resources/db/migration` (Flyway)
