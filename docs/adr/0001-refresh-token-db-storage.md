# 0001. Refresh 토큰을 DB 테이블에 저장

- **Status**: Accepted (2026-06-18)

## Context
로그아웃(US-02)은 "refresh 토큰 무효화"를 요구한다. 무상태 JWT만으로는 서버가 발급한 토큰을 강제로 무효화할 수 없다. 용어사전은 refresh 저장소를 "2차"로 미뤘으나 API 명세(기획서 6.2)는 logout·refresh를 MVP에 포함해 모순이 있었고, 테이블정의서에 refresh 토큰 테이블이 없었다.

## Decision
`refresh_token` 테이블을 신설한다. **사용자당 1행**(`user_id` 유니크)으로, 로그인 시 저장/회전, 재발급 시 회전, 로그아웃 시 삭제한다.

## Consequences
- (+) 로그아웃으로 즉시 무효화 가능, 재발급 시 회전(rotation)으로 탈취 위험 완화
- (−) DB 의존이 추가된다. **사용자당 1세션**만 지원(멀티 디바이스 미지원) — 필요 시 `(user_id, token)` 다행 구조로 확장
- 현재 **평문 저장** → 해시 저장 강화는 후속 과제 (issue #8)
