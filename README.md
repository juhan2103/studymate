# studymate — 스터디 모임 모집 플랫폼

소셜 로그인 기반의 스터디 모임 모집·참여 서비스. 실무 개발 흐름(설계 → 문서화 → 구현 → 테스트 → PR)을 연습하는 **학습용 프로젝트**다.

## 스택

- **Spring Boot 4.0.x** · Java 17 · Spring Security 7 · JPA(Hibernate 7) · MySQL 8
- 인증: **OAuth2(구글) + JWT(access/refresh) 직접 발급**
- 빌드 Gradle · 포맷 Spotless(Google Java Format) · API 문서 springdoc(Swagger)

## 사전 준비

- JDK 17, Docker
- 구글 OAuth 클라이언트(Google Cloud Console) — **승인된 리디렉션 URI**에 `http://localhost:8080/login/oauth2/code/google` 등록 + 동의 화면 테스트 사용자에 본인 계정 추가

## 설정

1. **docker-compose용**: `.env.example` → `.env` 복사 후 DB 값 채우기

   ```bash
   cp .env.example .env
   ```

2. **앱 비밀**: `src/main/resources/application-local.yml` 에 DB·구글 OAuth·`jwt.secret` 주입 (gitignore)
   - `jwt.secret` 생성: `openssl rand -base64 32`
   - 전체 환경변수 목록은 `docs/studymate_plan.md` 8장 참조

## 실행

```bash
docker compose up -d        # MySQL (호스트 포트 13306)
./gradlew bootRun           # 앱 (포트 8080)
```

- 로그인: 브라우저에서 `http://localhost:8080/oauth2/authorization/google`
- API 문서: `http://localhost:8080/swagger-ui.html`

## 테스트 · 포맷

```bash
./gradlew test              # 전체 테스트 (H2 in-memory)
./gradlew spotlessApply     # 커밋 전 포맷
```

## 문서

| 문서 | 내용 |
|------|------|
| `docs/studymate_dictionary.md` | 용어 사전(표준 어휘·네이밍) |
| `docs/studymate_plan.md` | 기획서(기능·API·권한·상태전이·환경) |
| `docs/studymate_table_spec.md` | 테이블 정의서(컬럼·인덱스·삭제정책) |
| `docs/studymate_convention.md` | 개발 컨벤션(패키지·코드·Git) |
| `docs/adr/` | 기술 결정 기록(왜 그렇게 했나) |
| `CLAUDE.md` | Claude Code 작업 가이드 |
