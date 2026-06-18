package com.studymate.auth.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class RefreshCookieManagerTest {

  private final RefreshCookieManager manager = new RefreshCookieManager(true);

  @Test
  void create_httpOnly_secure_쿠키를_만든다() {
    ResponseCookie cookie = manager.create("refresh-token", 1000);

    assertThat(cookie.getName()).isEqualTo("refreshToken");
    assertThat(cookie.getValue()).isEqualTo("refresh-token");
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isTrue();
    assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(1000);
  }

  @Test
  void clear_maxAge가_0인_빈_쿠키를_만든다() {
    ResponseCookie cookie = manager.clear();

    assertThat(cookie.getValue()).isEmpty();
    assertThat(cookie.getMaxAge().getSeconds()).isZero();
  }
}
