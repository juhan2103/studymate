package com.studymate.global.security;

import com.studymate.user.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/** OAuth2 로그인 핸드셰이크 동안 사용하는 principal (userId·role 보유). */
public class UserPrincipal implements OAuth2User {

  private final Long userId;
  private final Role role;
  private final Map<String, Object> attributes;

  public UserPrincipal(Long userId, Role role, Map<String, Object> attributes) {
    this.userId = userId;
    this.role = role;
    this.attributes = attributes;
  }

  public Long getUserId() {
    return userId;
  }

  public Role getRole() {
    return role;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getName() {
    return String.valueOf(userId);
  }
}
