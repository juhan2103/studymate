package com.studymate.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.studymate.auth.domain.RefreshToken;
import com.studymate.global.config.JpaConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

  @Autowired private RefreshTokenRepository repository;

  private RefreshToken token(Long userId, String value) {
    return RefreshToken.builder()
        .userId(userId)
        .token(value)
        .expiresAt(LocalDateTime.now().plusDays(14))
        .build();
  }

  @Test
  void userId와_token으로_조회한다() {
    repository.save(token(1L, "rt-1"));

    assertThat(repository.findByUserId(1L)).isPresent();
    assertThat(repository.findByToken("rt-1")).isPresent();
  }

  @Test
  void userId로_삭제한다() {
    repository.save(token(1L, "rt-1"));

    repository.deleteByUserId(1L);

    assertThat(repository.findByUserId(1L)).isEmpty();
  }
}
