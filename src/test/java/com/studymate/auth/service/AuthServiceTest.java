package com.studymate.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.studymate.auth.domain.RefreshToken;
import com.studymate.auth.repository.RefreshTokenRepository;
import com.studymate.global.exception.BusinessException;
import com.studymate.global.security.JwtTokenProvider;
import com.studymate.loginhistory.domain.LoginHistory;
import com.studymate.loginhistory.repository.LoginHistoryRepository;
import com.studymate.user.domain.Provider;
import com.studymate.user.domain.Role;
import com.studymate.user.domain.User;
import com.studymate.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

  private JwtTokenProvider tokenProvider;
  private RefreshTokenRepository refreshTokenRepository;
  private LoginHistoryRepository loginHistoryRepository;
  private UserRepository userRepository;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    tokenProvider = mock(JwtTokenProvider.class);
    refreshTokenRepository = mock(RefreshTokenRepository.class);
    loginHistoryRepository = mock(LoginHistoryRepository.class);
    userRepository = mock(UserRepository.class);
    authService =
        new AuthService(
            tokenProvider, refreshTokenRepository, loginHistoryRepository, userRepository);
  }

  @Test
  void 로그인시_토큰발급하고_refresh저장하며_로그인이력을_기록한다() {
    given(tokenProvider.createAccessToken(1L, "USER")).willReturn("access");
    given(tokenProvider.createRefreshToken(1L)).willReturn("refresh");
    given(tokenProvider.refreshExpiryAt()).willReturn(LocalDateTime.now().plusDays(14));
    given(tokenProvider.refreshExpirationSeconds()).willReturn(1_209_600L);
    given(refreshTokenRepository.findByUserId(1L)).willReturn(Optional.empty());

    AuthService.IssuedTokens tokens = authService.issueOnLogin(1L, "USER", "127.0.0.1", "JUnit");

    assertThat(tokens.accessToken()).isEqualTo("access");
    assertThat(tokens.refreshToken()).isEqualTo("refresh");
    verify(refreshTokenRepository).save(any(RefreshToken.class));
    verify(loginHistoryRepository).save(any(LoginHistory.class));
  }

  @Test
  void refresh_유효하지_않은_토큰이면_예외() {
    given(tokenProvider.isValid("bad")).willReturn(false);

    assertThatThrownBy(() -> authService.refresh("bad")).isInstanceOf(BusinessException.class);
  }

  @Test
  void refresh_DB에_없는_토큰이면_예외() {
    given(tokenProvider.isValid("refresh")).willReturn(true);
    given(refreshTokenRepository.findByToken("refresh")).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.refresh("refresh")).isInstanceOf(BusinessException.class);
  }

  @Test
  void refresh_정상이면_새_access를_발급하고_토큰을_회전한다() {
    RefreshToken saved =
        RefreshToken.builder()
            .userId(1L)
            .token("refresh")
            .expiresAt(LocalDateTime.now().plusDays(1))
            .build();
    User user =
        User.builder()
            .email("a@b.com")
            .name("n")
            .provider(Provider.GOOGLE)
            .providerId("pid")
            .role(Role.USER)
            .build();
    given(tokenProvider.isValid("refresh")).willReturn(true);
    given(refreshTokenRepository.findByToken("refresh")).willReturn(Optional.of(saved));
    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(tokenProvider.createAccessToken(1L, "USER")).willReturn("newAccess");
    given(tokenProvider.createRefreshToken(1L)).willReturn("newRefresh");
    given(tokenProvider.refreshExpiryAt()).willReturn(LocalDateTime.now().plusDays(14));
    given(tokenProvider.refreshExpirationSeconds()).willReturn(1_209_600L);

    AuthService.IssuedTokens tokens = authService.refresh("refresh");

    assertThat(tokens.accessToken()).isEqualTo("newAccess");
    assertThat(tokens.refreshToken()).isEqualTo("newRefresh");
    assertThat(saved.getToken()).isEqualTo("newRefresh");
  }

  @Test
  void logout_저장된_refresh토큰을_삭제한다() {
    authService.logout(1L);

    verify(refreshTokenRepository).deleteByUserId(1L);
  }
}
