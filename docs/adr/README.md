# Architecture Decision Records (ADR)

주요 기술 결정을 기록한다. 코드만 봐서는 "왜 이렇게 했는지"가 드러나지 않는 결정을 남긴다.

- 파일명: `NNNN-제목.md` (4자리 일련번호)
- 각 ADR: **Status · Context · Decision · Consequences**
- 되돌린 결정은 Status를 `Superseded by NNNN`으로 바꾸고 새 ADR로 대체한다.

| # | 결정 | Status |
|---|------|--------|
| [0001](0001-refresh-token-db-storage.md) | Refresh 토큰을 DB 테이블에 저장 | Accepted |
| [0002](0002-jwt-library-jjwt-gson.md) | JWT 라이브러리는 jjwt + gson 직렬화 | Accepted |
| [0003](0003-oauth2-token-delivery.md) | OAuth2 콜백 후 토큰 전달 방식 | Accepted |
