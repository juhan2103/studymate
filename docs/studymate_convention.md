# 스터디 모임 모집 플랫폼 — 개발 컨벤션

> 코드를 어떻게 짜고 커밋하느냐에 대한 규약. API 동작 규약(응답·에러·페이징)은 `기획서` 7번 참조.
> 솔로 학습 프로젝트지만 실무 흐름을 연습하는 것이 목적이므로 가볍게라도 지킨다.

---

## 1. 패키지 구조 (도메인형)

기능(도메인) 단위로 묶는 도메인형을 사용한다. 기능 추가 시 해당 도메인 폴더만 건드리면 된다.

```
com.studymate
├── global
│   ├── config        # JPA Auditing 등 설정
│   ├── security      # OAuth2, JWT 필터·토큰 제공자, SecurityConfig
│   ├── exception     # 전역 예외 핸들러, ErrorCode enum
│   └── common        # BaseTimeEntity 등 공통
├── auth              # 로그인 / 토큰 발급·재발급·로그아웃
├── user
│   ├── domain        # User 엔티티
│   ├── repository
│   ├── service
│   ├── controller
│   └── dto
├── study             # (user와 동일한 하위 구조)
├── membership
├── comment
└── loginhistory      # 로그인 이력 (기록 전용)
```

> 계층형(최상위를 controller/service/repository로 분리)은 작은 프로젝트엔 편하지만 도메인이 늘면 관련 코드가 흩어진다. 도메인형이 확장에 유리.

## 2. 코드 컨벤션

- **스타일**: Google Java Style 기준. 포매터는 Spotless(Gradle 플러그인) 또는 IntelliJ 포매터로 자동화. 커밋 전 `./gradlew spotlessApply`.
- **네이밍**: 클래스 `PascalCase`, 메서드·변수 `camelCase`, 상수 `UPPER_SNAKE_CASE`, 패키지 `lowercase`.
- **계층 책임 분리**:
  - Controller — 요청/응답 매핑, 입력 검증
  - Service — 비즈니스 로직, 트랜잭션 경계(`@Transactional`)
  - Repository — 데이터 접근
- **엔티티 노출 금지**: 컨트롤러는 항상 DTO로 주고받는다. 엔티티를 직접 직렬화하지 않는다.
- **Lombok 사용 범위**: 엔티티는 `@Getter`, `@Builder`, `@NoArgsConstructor(access = PROTECTED)`까지만. `@Setter`·`@Data`는 엔티티에 지양(캡슐화·불변성).
- **DTO**: `record` 활용 권장 (불변 요청/응답 객체).
- **검증**: 컨트롤러 진입 DTO에 Bean Validation(`@NotNull`, `@Size`, `@Positive` 등).
- **네이밍 규칙은 용어 사전을 따른다** (모임=study, 모임장=leader 등).

## 3. Git 컨벤션

### 3.1 브랜치 전략 — GitHub Flow

솔로 프로젝트에 적정한 단순 전략. Git Flow(develop/release/hotfix)는 과하다.

- `main` — 항상 배포 가능한 상태 유지. **`main`에는 직접 commit·merge·push 금지** (로컬 hook으로도 차단)
- 작업 브랜치 → PR → `main`. **1 기능 = 1 브랜치 = 1 PR.**
  - `feat/*` 기능, `fix/*` 버그, `docs/*` 문서, `refactor/*` 리팩터링, `chore/*` 설정·잡무
- **머지는 PR을 통해서만** — `gh pr merge`(서버측). 로컬 `git merge`로 main에 직접 반영하지 않는다.

### 3.2 커밋 메시지 — Conventional Commits

형식: `type(scope): 제목`

- **type**: `feat` `fix` `docs` `refactor` `test` `chore` `style`
- **scope**: 도메인/영역 (`study`, `membership`, `auth`, `global` 등)
- **커밋은 논리 단위로 분리한다** — 구현을 끝에 한 커밋으로 몰지 말고 단계마다 커밋, 순서는 컴파일 의존성 순 (PR이 하나여도 적용)
- 예시:
  - `feat(study): 모임 생성 API 구현`
  - `fix(membership): 중복 신청 검증 추가`
  - `chore(docker): MySQL compose 설정 추가`
  - `docs(readme): 실행 방법 작성`

### 3.3 PR 습관

- 기능 단위로 PR 생성 (셀프 머지라도). 실무 흐름 연습 + 변경 이력 정리.
- PR 제목도 커밋 컨벤션을 따른다.
- 가능하면 단계(1단계 세팅, 2단계 Swagger 등)별로 브랜치를 끊어 진행.

### 3.4 .gitignore

빌드 산출물·IDE 파일·비밀 정보는 절대 커밋하지 않는다.

```
build/
.gradle/
*.class
.idea/
*.iml
.env
application-secret.yml   # 또는 application-local.yml 등 비밀 설정
```

> 환경변수·비밀키로 무엇을 분리할지는 `기획서` 8번 환경/설정 항목에서 확정.

---

## 4. 작업 흐름 (파이프라인)

기능 구현은 **분석 → 설계 → 구현 → 검증** 순서로 진행하고, 단계를 한 번에 몰아서 실행하지 않는다. 각 단계가 끝나면 결과를 보고하고 다음 단계 진행을 확인받는다.

- **분석**: 해당 사양 절만 읽고 범위·영향 파일 파악 → 범위 확인
- **설계**: 패키지·DTO·엔티티·에러포맷 설계 → 승인 (Claude는 Plan 모드로 강제)
- **구현**: `feat/*` 브랜치에서 규약 준수, 논리 단위로 커밋
- **검증**: `./gradlew spotlessApply` → `build`/`test` 통과 → `code-reviewer` 에이전트로 코드리뷰 → 커밋·PR

> CLAUDE.md에는 이 흐름과 Git 규약의 **요약(운영 지침)** 이 있고, **상세·근거는 본 문서**를 따른다.
