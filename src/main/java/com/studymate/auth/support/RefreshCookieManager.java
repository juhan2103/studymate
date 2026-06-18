package com.studymate.auth.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/** refresh 토큰을 담는 httpOnly 쿠키 생성·삭제. */
@Component
public class RefreshCookieManager {

  public static final String COOKIE_NAME = "refreshToken";

  private final boolean secure;

  public RefreshCookieManager(@Value("${app.cookie.secure}") boolean secure) {
    this.secure = secure;
  }

  public ResponseCookie create(String token, long maxAgeSeconds) {
    return base(token).maxAge(maxAgeSeconds).build();
  }

  public ResponseCookie clear() {
    return base("").maxAge(0).build();
  }

  private ResponseCookie.ResponseCookieBuilder base(String value) {
    return ResponseCookie.from(COOKIE_NAME, value)
        .httpOnly(true)
        .secure(secure)
        .path("/")
        .sameSite("Lax");
  }
}
