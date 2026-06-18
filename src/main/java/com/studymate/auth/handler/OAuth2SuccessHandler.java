package com.studymate.auth.handler;

import com.studymate.auth.service.AuthService;
import com.studymate.auth.service.AuthService.IssuedTokens;
import com.studymate.auth.support.RefreshCookieManager;
import com.studymate.global.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** 로그인 성공 → 토큰 발급, refresh 쿠키 set, access를 프론트로 리다이렉트. */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthService authService;
  private final RefreshCookieManager cookieManager;
  private final String redirectUri;

  public OAuth2SuccessHandler(
      AuthService authService,
      RefreshCookieManager cookieManager,
      @Value("${app.frontend.redirect-uri}") String redirectUri) {
    this.authService = authService;
    this.cookieManager = cookieManager;
    this.redirectUri = redirectUri;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    IssuedTokens tokens =
        authService.issueOnLogin(
            principal.getUserId(),
            principal.getRole().name(),
            request.getRemoteAddr(),
            request.getHeader("User-Agent"));

    response.addHeader(
        HttpHeaders.SET_COOKIE,
        cookieManager.create(tokens.refreshToken(), tokens.refreshMaxAgeSeconds()).toString());

    String target =
        UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("accessToken", tokens.accessToken())
            .build()
            .toUriString();
    getRedirectStrategy().sendRedirect(request, response, target);
  }
}
