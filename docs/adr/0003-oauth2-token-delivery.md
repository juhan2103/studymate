# 0003. OAuth2 콜백 후 토큰 전달 방식

- **Status**: Accepted (2026-06-18)

## Context
OAuth2 로그인 성공은 브라우저 리다이렉트로 끝나므로, SPA 프론트(`localhost:5173`)에 발급한 JWT를 어떻게 넘길지 정해야 했다. 후보: (a) 리다이렉트 쿼리에 access·refresh 둘 다, (b) refresh 쿠키 + access 쿼리, (c) 콜백에서 JSON 응답.

## Decision
**refresh = httpOnly·Secure 쿠키**, **access = 프론트 리다이렉트 URL 쿼리 파라미터**(`app.frontend.redirect-uri?accessToken=...`)로 전달한다.

## Consequences
- (+) refresh가 JS·URL·브라우저 히스토리에 노출되지 않음(httpOnly) → XSS·유출 위험 완화
- (−) access는 리다이렉트 URL에 잠깐 노출되나 단기 토큰이라 허용. 프론트는 access를 메모리에 보관하고, refresh는 쿠키로 자동 동봉되는 것을 전제로 한다
- 쿠키 `secure` 플래그는 `app.cookie.secure`로 환경별 제어(로컬 false / 운영 true)
