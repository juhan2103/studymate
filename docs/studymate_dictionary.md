# 스터디 모임 모집 플랫폼 — 용어 사전 (Glossary)

> 이 프로젝트에서 쓰는 표준 어휘 + 표기 규칙 + 축약어를 정의한다.
> 엔티티 · 변수 · 메서드 · API 경로 네이밍은 모두 이 문서를 따른다. (DB 컬럼 상세는 `테이블 정의서` 참조)

---

## 엔티티 (Entity)

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 엔티티 | 사용자 | user | - | 소셜 로그인으로 가입한 서비스 이용자. 모든 활동의 기준이 되는 핵심 엔티티. `user`는 SQL 예약어/함수(USER())와 충돌하므로 **테이블명은 `users` 사용**(orders와 동일한 이유). | `users` 테이블 |
| 엔티티 | 모임 | study | - | 사용자가 개설하는 스터디 모임. `group`은 SQL 예약어이자 의미가 모호하여 지양, **`study` 사용**. | `study` 테이블 |
| 엔티티 | 참가 신청 | membership | - | 사용자와 모임을 잇는 신청/참여 기록. user ↔ study N:M 관계의 중간 엔티티. | `membership` 테이블 |
| 엔티티 | 댓글 | comment | - | 모임에 작성하는 글. | `comment` 테이블 |

## 역할 (Role)

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 역할 | 모임장 | leader | - | 모임을 개설한 사용자. 해당 모임의 운영(수정·삭제·승인) 권한 보유. | `study.leader_id` |
| 역할 | 참여자 | participant | - | 신청이 승인되어 모임에 속한 활성 사용자. 별도 테이블이 아니라 상태로 표현. | `membership.status = APPROVED AND deleted_at IS NULL` |
| 역할 | 비회원 | guest | - | 로그인하지 않은 방문자. 모임 조회만 가능. | - |
| 역할 | 관리자 | administrator | admin | 시스템 관리 권한을 가진 사용자. | `users.role = ADMIN` |

## 주요 속성 (Attribute)

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 속성 | 식별자 | identifier | id | 데이터를 고유하게 식별하는 PK. PK는 `id`, 외래 키는 `[엔티티]_id` 형식 사용. | 각 테이블 `id`, `user_id`, `study_id` |
| 속성 | 제목 | title | - | 모임의 제목. (사람·상품의 name과 구분해 모임은 title 사용) | `study.title` |
| 속성 | 이름 | name | - | 사용자의 표시 이름. | `users.name` |
| 속성 | 소개 | description | desc | 모임 상세 설명. `desc`는 SQL 예약어(정렬)라 컬럼·변수는 **`description` 사용**. | `study.description` |
| 속성 | 정원 | capacity | - | 모임의 최대 인원(모임장 제외). | `study.capacity` |
| 속성 | 내용 | content | - | 댓글 본문. | `comment.content` |
| 속성 | 제공자 | provider | - | 소셜 로그인 제공 업체 식별값. | `users.provider` |
| 속성 | 소프트 삭제 | soft delete | - | 행을 물리 삭제하지 않고 `deleted_at`에 시각을 기록해 '삭제됨'으로 표시하는 방식. 조회는 `deleted_at IS NULL`만 노출. | `deleted_at` 컬럼 |
| 속성 | 활성 멤버 | active member | - | `status = APPROVED` 이고 `deleted_at IS NULL`인 참여자. 정원 카운트 기준. | `membership` |

## 상태값 (Status)

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 상태 | 모집 상태 | recruit status | - | 모임이 모집 중인지 마감인지. `RECRUITING` / `CLOSED`. | `study.recruit_status` |
| 상태 | 신청 상태 | application status | - | 참가 신청의 처리 단계. `PENDING` / `APPROVED` / `REJECTED`. | `membership.status` |
| 상태 | 승인 | approve | - | 모임장이 신청을 받아들이는 행위(→ APPROVED). | `membership.status` |
| 상태 | 거절 | reject | - | 모임장이 신청을 반려하는 행위(→ REJECTED). | `membership.status` |

## 인증 · 보안 (Auth)

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 인증 | 인증 | authentication | authN | "누구인지" 확인하는 절차. 소셜 로그인으로 처리. | Spring Security, OAuth2 |
| 인증 | 인가 | authorization | authZ | "그 작업을 할 권한이 있는지" 확인하는 절차. | Spring Security |
| 인증 | 소셜 로그인 | social login | OAuth2 | 외부 제공자(Google 등)에 인증을 위임하는 방식. | Google OAuth2 (추후 Kakao·Naver) |
| 인증 | 제공자 | provider | - | 소셜 로그인 업체. `GOOGLE` / `KAKAO` / `NAVER`. | `users.provider` |
| 인증 | 액세스 토큰 | access token | - | 인증에 사용하는 단기 JWT. | JWT |
| 인증 | 리프레시 토큰 | refresh token | - | 액세스 토큰 재발급용 장기 토큰. | refresh token 저장소 (2차) |
| 인증 | 로그인 이력 | login history | - | 사용자 로그인 발생 기록. 보안 감사 및 MAU 집계에 사용. | `login_history` 테이블 |

## 비즈니스 지표 (Metric) — 향후 통계/분석용

| 분류 | 명칭 | 전체 영문명 | 축약어 | 설명 | 관련 시스템 요소 |
|------|------|------------|--------|------|------------------|
| 지표 | 월간 활성 사용자 | Monthly Active Users | MAU | 한 달간 1회 이상 접속한 중복 없는 사용자 수. 서비스 활성화 핵심 지표. | `login_history` 기반 집계 |
| 지표 | 모임 개설 수 | created studies | - | 기간 내 새로 생성된 모임 수. | `study` 기반 집계 |
| 지표 | 신청 승인율 | approval rate | - | 전체 신청 중 승인된 비율. (APPROVED 수 / 전체 신청 수) | `membership` 기반 집계 |
| 지표 | 모임 충원율 | fill rate | - | 정원 대비 승인된 멤버 비율. 모집 효율 지표. | `study`, `membership` 기반 집계 |

---

## 표기 규칙 요약

- **테이블명**: 단수형 사용. 단, SQL 예약어와 충돌하면 회피 → `user` ❌ `users` ✅ (orders와 동일 원칙).
- **PK**: 항상 `id`. **FK**: `[참조엔티티]_id` (예: `leader_id`, `study_id`, `user_id`).
- **예약어 회피**: `desc` → `description`, `order` → `orders`, `user` → `users`.
- **상태값(enum)**: 영대문자 SNAKE_CASE (`RECRUITING`, `IN_PROGRESS` 등).
- **모임의 제목은 `title`, 사람의 이름은 `name`** 으로 구분.
