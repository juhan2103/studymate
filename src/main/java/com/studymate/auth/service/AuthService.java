package com.studymate.auth.service;

import com.studymate.auth.domain.RefreshToken;
import com.studymate.auth.repository.RefreshTokenRepository;
import com.studymate.global.exception.BusinessException;
import com.studymate.global.exception.ErrorCode;
import com.studymate.global.security.JwtTokenProvider;
import com.studymate.loginhistory.domain.LoginHistory;
import com.studymate.loginhistory.repository.LoginHistoryRepository;
import com.studymate.user.domain.User;
import com.studymate.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 로그인 발급·refresh 회전·로그아웃 로직. */
@Service
public class AuthService {

  private final JwtTokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final LoginHistoryRepository loginHistoryRepository;
  private final UserRepository userRepository;

  public AuthService(
      JwtTokenProvider tokenProvider,
      RefreshTokenRepository refreshTokenRepository,
      LoginHistoryRepository loginHistoryRepository,
      UserRepository userRepository) {
    this.tokenProvider = tokenProvider;
    this.refreshTokenRepository = refreshTokenRepository;
    this.loginHistoryRepository = loginHistoryRepository;
    this.userRepository = userRepository;
  }

  /** 발급된 토큰 묶음. refreshMaxAgeSeconds는 쿠키 maxAge용. */
  public record IssuedTokens(String accessToken, String refreshToken, long refreshMaxAgeSeconds) {}

  /** 로그인 성공: access/refresh 발급, refresh 저장(회전), 로그인 이력 기록. */
  @Transactional
  public IssuedTokens issueOnLogin(Long userId, String role, String ip, String userAgent) {
    String access = tokenProvider.createAccessToken(userId, role);
    String refresh = tokenProvider.createRefreshToken(userId);
    saveOrRotate(userId, refresh);
    loginHistoryRepository.save(
        LoginHistory.builder().userId(userId).ipAddress(ip).userAgent(userAgent).build());
    return new IssuedTokens(access, refresh, tokenProvider.refreshExpirationSeconds());
  }

  /** refresh 쿠키로 access 재발급 + refresh 회전. */
  @Transactional
  public IssuedTokens refresh(String refreshToken) {
    if (refreshToken == null || !tokenProvider.isValid(refreshToken)) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    RefreshToken saved =
        refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
    if (saved.isExpired()) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    Long userId = saved.getUserId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
    String access = tokenProvider.createAccessToken(userId, user.getRole().name());
    String newRefresh = tokenProvider.createRefreshToken(userId);
    saved.rotate(newRefresh, tokenProvider.refreshExpiryAt());
    return new IssuedTokens(access, newRefresh, tokenProvider.refreshExpirationSeconds());
  }

  /** 로그아웃: 저장된 refresh 토큰 삭제로 무효화. */
  @Transactional
  public void logout(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  private void saveOrRotate(Long userId, String refresh) {
    refreshTokenRepository
        .findByUserId(userId)
        .ifPresentOrElse(
            rt -> rt.rotate(refresh, tokenProvider.refreshExpiryAt()),
            () ->
                refreshTokenRepository.save(
                    RefreshToken.builder()
                        .userId(userId)
                        .token(refresh)
                        .expiresAt(tokenProvider.refreshExpiryAt())
                        .build()));
  }
}
