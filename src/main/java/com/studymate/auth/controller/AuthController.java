package com.studymate.auth.controller;

import com.studymate.auth.dto.AccessTokenResponse;
import com.studymate.auth.service.AuthService;
import com.studymate.auth.service.AuthService.IssuedTokens;
import com.studymate.auth.support.RefreshCookieManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 토큰 재발급·로그아웃 (로그인 시작/콜백은 Spring Security가 처리). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final RefreshCookieManager cookieManager;

  public AuthController(AuthService authService, RefreshCookieManager cookieManager) {
    this.authService = authService;
    this.cookieManager = cookieManager;
  }

  @PostMapping("/refresh")
  public ResponseEntity<AccessTokenResponse> refresh(
      @CookieValue(name = RefreshCookieManager.COOKIE_NAME, required = false) String refreshToken) {
    IssuedTokens tokens = authService.refresh(refreshToken);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            cookieManager.create(tokens.refreshToken(), tokens.refreshMaxAgeSeconds()).toString())
        .body(new AccessTokenResponse(tokens.accessToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId) {
    authService.logout(userId);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, cookieManager.clear().toString())
        .build();
  }
}
