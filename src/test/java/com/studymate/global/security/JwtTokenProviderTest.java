package com.studymate.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static final String SECRET = "test-secret-test-secret-test-secret-1234";

  private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3_600_000, 1_209_600_000);

  @Test
  void access_토큰을_발급하고_userId와_role을_파싱한다() {
    String token = provider.createAccessToken(7L, "USER");

    Claims claims = provider.parse(token);

    assertThat(claims.getSubject()).isEqualTo("7");
    assertThat(claims.get("role", String.class)).isEqualTo("USER");
    assertThat(provider.getUserId(token)).isEqualTo(7L);
    assertThat(provider.isValid(token)).isTrue();
  }

  @Test
  void refresh_토큰에는_role이_없다() {
    String token = provider.createRefreshToken(7L);

    assertThat(provider.parse(token).get("role", String.class)).isNull();
    assertThat(provider.isValid(token)).isTrue();
  }

  @Test
  void 만료된_토큰은_유효하지_않다() {
    JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1000, -1000);
    String token = expiredProvider.createAccessToken(7L, "USER");

    assertThat(expiredProvider.isValid(token)).isFalse();
  }

  @Test
  void 다른_키로_서명된_토큰은_유효하지_않다() {
    JwtTokenProvider other =
        new JwtTokenProvider("another-secret-another-secret-aaaa-9999", 3_600_000, 1_209_600_000);
    String token = other.createAccessToken(7L, "USER");

    assertThat(provider.isValid(token)).isFalse();
  }
}
