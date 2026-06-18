package com.studymate.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.studymate.global.config.JpaConfig;
import com.studymate.user.domain.Provider;
import com.studymate.user.domain.Role;
import com.studymate.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  private User newUser(String email, String providerId) {
    return User.builder()
        .email(email)
        .name("tester")
        .provider(Provider.GOOGLE)
        .providerId(providerId)
        .role(Role.USER)
        .build();
  }

  @Test
  void provider와_providerId로_조회한다() {
    userRepository.save(newUser("a@b.com", "google-1"));

    assertThat(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "google-1")).isPresent();
    assertThat(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "none")).isEmpty();
  }

  @Test
  void 이메일로_조회한다() {
    userRepository.save(newUser("a@b.com", "google-1"));

    assertThat(userRepository.findByEmail("a@b.com")).isPresent();
  }

  @Test
  void 같은_provider_providerId_조합은_중복저장할_수_없다() {
    userRepository.saveAndFlush(newUser("a@b.com", "dup"));

    assertThatThrownBy(() -> userRepository.saveAndFlush(newUser("c@d.com", "dup")))
        .isInstanceOf(Exception.class);
  }
}
