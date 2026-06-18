package com.studymate.auth.domain;

import com.studymate.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 사용자당 1건의 refresh 토큰 (로그아웃 시 삭제로 무효화). */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "refresh_token",
    uniqueConstraints = @UniqueConstraint(name = "uq_refresh_user", columnNames = "user_id"))
public class RefreshToken extends BaseCreatedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false, length = 512)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Builder
  private RefreshToken(Long userId, String token, LocalDateTime expiresAt) {
    this.userId = userId;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  /** 토큰 회전: 같은 행을 새 토큰·만료로 갱신한다. */
  public void rotate(String token, LocalDateTime expiresAt) {
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(LocalDateTime.now());
  }
}
