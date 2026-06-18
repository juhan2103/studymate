package com.studymate.loginhistory.domain;

import com.studymate.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 로그인 성공 이력 (append-only, soft delete 없음 — 테이블정의서 login_history). */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "login_history",
    indexes = @Index(name = "idx_login_user_created", columnList = "user_id, created_at"))
public class LoginHistory extends BaseCreatedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Builder
  private LoginHistory(Long userId, String ipAddress, String userAgent) {
    this.userId = userId;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }
}
