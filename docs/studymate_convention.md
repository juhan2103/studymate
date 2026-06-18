# 스터디 모임 모집 플랫폼 — 개발 컨벤션

> 코드를 어떻게 짜고 커밋하느냐에 대한 규약. API 동작 규약(응답·에러·페이징)은 `기획서` 7번 참조.
> 솔로 학습 프로젝트지만 실무 흐름을 연습하는 것이 목적이므로 가볍게라도 지킨다.

---

## 1. 패키지 구조 (도메인형)

기능(도메인) 단위로 묶는 도메인형을 사용한다. 기능 추가 시 해당 도메인 폴더만 건드리면 된다.

```
com.example.studyplatform
├── global
│   ├── config        # Security, JPA Auditing, Swagger 설정
│   ├── security      # OAuth2, JWT 필터·토큰 제공자
│   ├── exception     # 전역 예외 핸들러, ErrorCode enum
│   └── common        # 공통 응답, BaseTimeEntity 등
├── auth              # 로그인 / 토큰 발급·재발급
├── user
│   ├── domain        # User 엔티티
│   ├── repository
│   ├── service
│   ├── controller
│   └── dto
├── study             # (user와 동일한 하위 구조)
├── membership
└── comment
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

- `main` — 항상 배포 가능한 상태 유지
- 작업 브랜치 → PR → `main` 머지
  - `feat/*` 기능, `fix/*` 버그, `docs/*` 문서, `refactor/*` 리팩터링, `chore/*` 설정·잡무

### 3.2 커밋 메시지 — Conventional Commits

형식: `type(scope): 제목`

- **type**: `feat` `fix` `docs` `refactor` `test` `chore` `style`
- **scope**: 도메인/영역 (`study`, `membership`, `auth`, `global` 등)
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
