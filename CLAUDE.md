# CLAUDE.md

이 저장소에서 작업할 때 Claude Code에게 주는 가이드. **상세 규칙은 docs/ 문서에 있다 — 여기서는 가리키기만 한다.**

## 명령

```bash
./gradlew bootRun                              # 실행
./gradlew build -x test                        # 빌드 (테스트 제외)
./gradlew test                                 # 전체 테스트
./gradlew test --tests "com.studymate.XTest"   # 단일 클래스
./gradlew spotlessApply                        # 커밋 전 포맷
```

Windows는 `.\gradlew.bat`. 포맷·코드 스타일은 Spotless가 강제하므로 여기서 다루지 않는다.

## 스택 — Spring Boot 4 (Boot 3 패턴 금지)

Spring Boot 4.0.x · Java 17 · Spring Security 7 · Jackson 3 · JPA(Hibernate 7) · MySQL · springdoc 3.x · Lombok.

- web = `spring-boot-starter-webmvc`, OAuth2 = `spring-boot-starter-security-oauth2-client` (Boot 4 모듈러 이름).
- 테스트 mock은 `@MockitoBean` / `@MockitoSpyBean` (`@MockBean` 제거됨).
- 인증 = OAuth2(구글) + JWT(access/refresh) 직접 발급.

## 사양 문서 (작업 전 해당 절만 읽어라)

- 네이밍 표준: @docs/studymate_dictionary.md  ← 항상 로딩
- 기능·API·권한·상태전이·정원규칙(R1~R5)·페이징·에러포맷: `docs/studymate_plan.md`
- DB 컬럼·인덱스·삭제정책: `docs/studymate_table_spec.md`
- 코드·Git 규약: `docs/studymate_convention.md`

작업할 기능의 해당 절만 읽는다. 예) 승인/거절(US-14) → 기획서 4.3·6.4 + 테이블정의서 `membership`.

## 패키지 = 도메인형 (계층형 금지)

`controller/`·`service/`·`repository/`를 최상위로 두지 않는다. 도메인 단위로 묶는다:

`com.studymate` 아래 → `global`(config · security · exception · common) + `auth` + 도메인별 `user` / `study` / `membership` / `comment` (각각 domain · repository · service · controller · dto).

## 핵심 규칙 (자주 어기고 툴이 못 잡는 것만)

- 네이밍은 용어사전 준수: 모임=`study`, 모임장=`leader`, 사용자 테이블=`users`, 모임 제목=`title`·사람 이름=`name`. PK=`id`, FK=`{엔티티}_id`.
- 컨트롤러는 엔티티 직렬화 금지 → 항상 DTO(`record`).
- 엔티티 Lombok은 `@Getter`/`@Builder`/`@NoArgsConstructor(PROTECTED)`까지만. `@Setter`·`@Data` 금지.
- 시각 컬럼은 `BaseTimeEntity` + JPA Auditing 자동.
- `study`·`membership`·`comment`는 soft delete(`deleted_at`). 조회는 항상 `deleted_at IS NULL`.
- 활성 멤버십 유일성은 서비스 계층에서 검증 (MySQL 부분 유니크 미지원).
- 미인증 → 401, 소유권 위반 → 403. "모임장만"은 행 단위 소유권 체크(`leader_id == 현재 사용자`).

그 외 상태 전이·정원 규칙·페이징 기본값·에러 바디 형식 등 상세는 위 사양 문서를 읽는다 (여기 중복 금지).

## 시크릿

DB·OAuth·JWT 비밀은 코드·깃에 하드코딩 금지. `application-local.yml` 또는 `.env`로 주입(둘 다 gitignore). MySQL은 `docker compose up -d`. `ddl-auto`: 로컬 `update` / 그 외 `validate`.

## Git

Conventional Commits `type(scope): 제목`. GitHub Flow: `feat/*` `fix/*` `docs/*` → PR → `main`.
