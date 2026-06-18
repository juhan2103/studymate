package com.studymate.auth.service;

import com.studymate.global.security.UserPrincipal;
import com.studymate.user.domain.Provider;
import com.studymate.user.domain.User;
import com.studymate.user.repository.UserRepository;
import java.util.Locale;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 구글 userinfo로 User를 upsert하고 UserPrincipal을 반환한다. */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Provider provider =
        Provider.valueOf(
            userRequest.getClientRegistration().getRegistrationId().toUpperCase(Locale.ROOT));
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // 구글 표준 속성
    String providerId = String.valueOf(attributes.get("sub"));
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String picture = (String) attributes.get("picture");

    User user =
        userRepository
            .findByProviderAndProviderId(provider, providerId)
            .map(
                existing -> {
                  existing.updateProfile(name, picture);
                  return existing;
                })
            .orElseGet(
                () ->
                    userRepository.save(
                        User.builder()
                            .email(email)
                            .name(name)
                            .profileImageUrl(picture)
                            .provider(provider)
                            .providerId(providerId)
                            .build()));

    return new UserPrincipal(user.getId(), user.getRole(), attributes);
  }
}
