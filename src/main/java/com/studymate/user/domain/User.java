package com.studymate.user.domain;

import com.studymate.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 소셜 로그인으로 가입한 사용자 (테이블정의서 users). */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "uq_email", columnNames = "email"),
      @UniqueConstraint(
          name = "uq_provider_provider_id",
          columnNames = {"provider", "provider_id"})
    })
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(name = "profile_image_url", length = 500)
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Provider provider;

  @Column(name = "provider_id", nullable = false, length = 255)
  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @Builder
  private User(
      String email,
      String name,
      String profileImageUrl,
      Provider provider,
      String providerId,
      Role role) {
    this.email = email;
    this.name = name;
    this.profileImageUrl = profileImageUrl;
    this.provider = provider;
    this.providerId = providerId;
    this.role = role == null ? Role.USER : role;
  }

  /** 소셜 프로필이 바뀌면 표시 이름·이미지를 최신화한다. */
  public void updateProfile(String name, String profileImageUrl) {
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }
}
