# 스터디 모임 모집 플랫폼 — 테이블 정의서

> DB 컬럼 수준의 상세 명세. 용어·표기 규칙은 `용어 사전`, 관계·설계 결정·ERD는 `기획서` 3번 참조.
> 공통: 모든 테이블 PK는 `id` (BIGINT, AUTO_INCREMENT). `created_at` / `updated_at`은 JPA Auditing으로 자동 기록.

---

## users — 사용자

**테이블 한글명** 사용자
**테이블 영문명** users  *(`user`는 SQL 예약어/USER() 함수와 충돌하여 복수형 사용)*
**테이블 설명** 소셜 로그인으로 가입한 사용자의 기본 정보를 저장하는 테이블

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 사용자 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 이메일 | email | VARCHAR(100) | UQ, NOT NULL | 소셜 계정 이메일 |
| 3 | 이름 | name | VARCHAR(50) | NOT NULL | 표시 이름 |
| 4 | 프로필 이미지 | profile_image_url | VARCHAR(500) | NULL | 소셜 프로필 이미지 URL |
| 5 | 제공자 | provider | VARCHAR(20) | NOT NULL | GOOGLE (추후 KAKAO, NAVER) |
| 6 | 제공자 ID | provider_id | VARCHAR(255) | NOT NULL | 제공자별 사용자 고유 식별자 |
| 7 | 권한 | role | VARCHAR(20) | NOT NULL, 기본값 'USER' | USER, ADMIN |
| 8 | 가입일 | created_at | DATETIME | NOT NULL | 가입 시점 (JPA Auditing) |
| 9 | 수정일 | updated_at | DATETIME | NOT NULL | 수정 시 자동 갱신 |

> 비밀번호 컬럼 없음 — 소셜 로그인만 사용하므로 자체 비밀번호를 저장하지 않는다.

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| uq_email | email | 이메일 중복 가입 방지 |
| uq_provider_provider_id | provider, provider_id | 동일 소셜 계정 중복 방지 / 로그인 시 사용자 식별 |

---

## study — 모임

**테이블 한글명** 모임
**테이블 영문명** study
**테이블 설명** 사용자가 개설한 스터디 모임 정보를 저장하는 테이블

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 모임 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 모임장 ID | leader_id | BIGINT | FK, NOT NULL | 개설자 (users.id 참조) |
| 3 | 제목 | title | VARCHAR(100) | NOT NULL | 모임 제목 |
| 4 | 소개 | description | TEXT | NULL | 모임 상세 설명 |
| 5 | 정원 | capacity | INT | NOT NULL | 최대 인원 (모임장 제외) |
| 6 | 모집 상태 | recruit_status | VARCHAR(20) | NOT NULL, 기본값 'RECRUITING' | RECRUITING, CLOSED |
| 7 | 생성일 | created_at | DATETIME | NOT NULL | 생성 시점 |
| 8 | 수정일 | updated_at | DATETIME | NOT NULL | 수정 시 자동 갱신 |
| 9 | 삭제일시 | deleted_at | DATETIME | NULL | soft delete 시각. NULL=존재, 값=삭제됨 |

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| fk_study_leader | leader_id | users 참조 외래 키 |
| idx_recruit_status_created_at | recruit_status, created_at | 모집중 목록을 최신순으로 조회하는 성능 향상용 복합 인덱스 |

---

## membership — 참가 신청

**테이블 한글명** 참가 신청
**테이블 영문명** membership
**테이블 설명** 사용자의 모임 참가 신청/참여 정보를 저장하는 테이블 (users ↔ study N:M 중간 테이블)

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 신청 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 모임 ID | study_id | BIGINT | FK, NOT NULL | 대상 모임 (study.id 참조) |
| 3 | 사용자 ID | user_id | BIGINT | FK, NOT NULL | 신청자 (users.id 참조) |
| 4 | 신청 상태 | status | VARCHAR(20) | NOT NULL, 기본값 'PENDING' | PENDING, APPROVED, REJECTED |
| 5 | 신청일 | created_at | DATETIME | NOT NULL | 신청 시점 |
| 6 | 수정일 | updated_at | DATETIME | NOT NULL | 상태 변경 시 자동 갱신 |
| 7 | 삭제일시 | deleted_at | DATETIME | NULL | soft delete 시각. NULL=활성, 값=취소·나감 |

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| (활성 유일성) | study_id, user_id WHERE deleted_at IS NULL | 한 모임에 활성 신청은 1건. **MySQL 조건부 유니크 미지원 → 서비스 계층에서 검증** (advanced: active_flag 트릭으로 DB 강제 가능) |
| idx_membership_study_status | study_id, status | 모임의 승인 멤버 수 집계 / 신청 목록 조회 |
| fk_membership_user | user_id | 내가 참여한 모임 조회 시 사용 |

---

## comment — 댓글

**테이블 한글명** 댓글
**테이블 영문명** comment
**테이블 설명** 모임에 작성된 댓글 정보를 저장하는 테이블

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 댓글 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 모임 ID | study_id | BIGINT | FK, NOT NULL | 댓글이 달린 모임 (study.id 참조) |
| 3 | 작성자 ID | user_id | BIGINT | FK, NOT NULL | 작성자 (users.id 참조) |
| 4 | 내용 | content | TEXT | NOT NULL | 댓글 본문 |
| 5 | 작성일 | created_at | DATETIME | NOT NULL | 작성 시점 |
| 6 | 수정일 | updated_at | DATETIME | NOT NULL | 수정 시 자동 갱신 |
| 7 | 삭제일시 | deleted_at | DATETIME | NULL | soft delete 시각 |

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| idx_comment_study | study_id | 모임별 댓글 목록 조회 성능 향상용 인덱스 |

---

## login_history — 로그인 이력

**테이블 한글명** 로그인 이력
**테이블 영문명** login_history
**테이블 설명** 사용자의 로그인 발생 기록을 저장하는 테이블 (보안 감사 / MAU 집계용)

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 이력 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 사용자 ID | user_id | BIGINT | FK, NOT NULL | 로그인한 사용자 (users.id 참조) |
| 3 | IP 주소 | ip_address | VARCHAR(45) | NULL | 접속 IP (IPv6 대응 길이 45) |
| 4 | User-Agent | user_agent | VARCHAR(500) | NULL | 접속 클라이언트(브라우저/기기) 정보 |
| 5 | 로그인 시각 | created_at | DATETIME | NOT NULL | 로그인 발생 시점 (JPA Auditing) |

> MVP에서는 **로그인 성공** 기록만 남긴다. 로그인 실패 추적은 소셜 로그인 특성상 provider가 처리하므로 2차로 미룬다.

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| idx_login_user_created | user_id, created_at | 내 로그인 이력 최신순 조회 / MAU 집계 성능 향상 |

---

## refresh_token — 리프레시 토큰

**테이블 한글명** 리프레시 토큰
**테이블 영문명** refresh_token
**테이블 설명** 액세스 토큰 재발급용 리프레시 토큰을 저장하는 테이블. 로그아웃 시 삭제로 무효화한다. (결정 근거: `docs/adr/0001`)

| No. | 컬럼 한글명 | 컬럼 영문명 | 데이터 타입 | 제약 조건 | 비고 |
|-----|------------|------------|------------|-----------|------|
| 1 | 토큰 ID | id | BIGINT | PK, AUTO_INCREMENT | 고유 식별자 (대리 키) |
| 2 | 사용자 ID | user_id | BIGINT | FK, UQ, NOT NULL | 소유 사용자 (users.id 참조). 사용자당 1건 |
| 3 | 토큰 | token | VARCHAR(512) | NOT NULL | refresh JWT 문자열 (해시 저장은 후속 과제) |
| 4 | 만료 시각 | expires_at | DATETIME | NOT NULL | 토큰 만료 시점 |
| 5 | 생성 시각 | created_at | DATETIME | NOT NULL | 발급/회전 시점 (JPA Auditing) |

> **사용자당 1세션**(`user_id` 유니크). 로그인·재발급 시 같은 행을 회전(rotate)하고, 로그아웃 시 삭제한다. 멀티 디바이스 세션은 미지원 (ADR-0001 참조).

**인덱스 / 제약**

| 인덱스명 | 컬럼 | 비고 |
|----------|------|------|
| uq_refresh_user | user_id | 사용자당 1개 토큰 (유니크) |

---

## 삭제 정책 (Soft Delete)

- **study · membership · comment 는 물리 삭제하지 않고 soft delete** (`deleted_at`에 시각 기록). 모든 조회는 `deleted_at IS NULL` 조건으로 필터한다. 구현: Hibernate 6 `@SoftDelete` 또는 `deleted_at` + `@SQLDelete` / `@Where`.
- **study 삭제(US-13) 시** 하위 `membership` · `comment`는 study가 비노출되면서 함께 가려진다. 물리 `ON DELETE CASCADE`는 사용하지 않는다.
- **활성 멤버십 유일성**(`study_id` + `user_id` 중 `deleted_at IS NULL` 1건)은 MySQL 조건부 유니크 미지원으로 **서비스 계층에서 검증**한다. (advanced: `active_flag` 컬럼 + 유니크 인덱스로 DB 강제 가능)
- **users 삭제**는 MVP 범위 밖 (소셜 로그인 탈퇴 플로우는 2차).
- **login_history 는 append-only 감사 로그**로 삭제하지 않는다 (soft delete 미적용).

## 2차 확장 후보 테이블

| 테이블 | 용도 | 비고 |
|--------|------|------|
| study_category | 모임 카테고리 · 태그 | 검색/필터 기능 도입 시 |
