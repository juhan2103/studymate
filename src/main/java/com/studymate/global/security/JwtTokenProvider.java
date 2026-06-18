package com.studymate.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** JWT(access/refresh) 발급·검증. HS256 서명. */
@Component
public class JwtTokenProvider {

  private static final String ROLE_CLAIM = "role";

  private final SecretKey key;
  private final long accessExpiration;
  private final long refreshExpiration;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-expiration}") long accessExpiration,
      @Value("${jwt.refresh-expiration}") long refreshExpiration) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessExpiration = accessExpiration;
    this.refreshExpiration = refreshExpiration;
  }

  public String createAccessToken(Long userId, String role) {
    return createToken(userId, role, accessExpiration);
  }

  public String createRefreshToken(Long userId) {
    return createToken(userId, null, refreshExpiration);
  }

  private String createToken(Long userId, String role, long expirationMs) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);
    var builder = Jwts.builder().subject(String.valueOf(userId)).issuedAt(now).expiration(expiry);
    if (role != null) {
      builder.claim(ROLE_CLAIM, role);
    }
    return builder.signWith(key).compact();
  }

  /** 유효한 토큰이면 claims 반환, 아니면 JwtException 전파. */
  public Claims parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public boolean isValid(String token) {
    try {
      parse(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Long getUserId(String token) {
    return Long.valueOf(parse(token).getSubject());
  }

  /** refresh 토큰 만료 시각 (DB 저장용). */
  public LocalDateTime refreshExpiryAt() {
    return LocalDateTime.now().plusSeconds(refreshExpirationSeconds());
  }

  /** refresh 만료(초) — 쿠키 maxAge용. */
  public long refreshExpirationSeconds() {
    return refreshExpiration / 1000;
  }
}
