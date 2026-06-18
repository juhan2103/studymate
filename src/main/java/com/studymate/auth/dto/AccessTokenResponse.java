package com.studymate.auth.dto;

/** access 토큰 재발급 응답. (refresh는 httpOnly 쿠키로 전달) */
public record AccessTokenResponse(String accessToken) {}
