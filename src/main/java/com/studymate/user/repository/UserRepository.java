package com.studymate.user.repository;

import com.studymate.user.domain.Provider;
import com.studymate.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByEmail(String email);
}
