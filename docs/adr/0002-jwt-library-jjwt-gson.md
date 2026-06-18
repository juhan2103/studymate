# 0002. JWT 라이브러리는 jjwt + gson 직렬화

- **Status**: Accepted (2026-06-18)

## Context
JWT(access/refresh)를 직접 발급·검증해야 하는데 의존성에 JWT 라이브러리가 없었다. Spring Boot 4는 Jackson 3(`tools.jackson`)를 사용하는데, jjwt의 기본 jackson 직렬화 모듈은 Jackson 2(`com.fasterxml`) 기반이라 버전 충돌 우려가 있었다.

## Decision
`io.jsonwebtoken:jjwt-api / jjwt-impl / jjwt-gson` 0.12.x를 사용한다. 직렬화는 Jackson과 무관한 **gson** 모듈로 처리해 Jackson 3와의 충돌을 원천 차단한다.

## Consequences
- (+) Boot 4의 Jackson 3와 독립적으로 동작 (빌드·실제 구글 로그인 e2e로 검증 완료)
- (−) gson 의존성이 추가된다. 추후 필요 시 Nimbus(JOSE) 등으로 교체 여지 있음
