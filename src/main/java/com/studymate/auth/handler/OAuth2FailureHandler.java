package com.studymate.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** 로그인 실패 → 프론트로 에러 리다이렉트. */
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final String redirectUri;

  public OAuth2FailureHandler(@Value("${app.frontend.redirect-uri}") String redirectUri) {
    this.redirectUri = redirectUri;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    String target =
        UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("error", "login_failed")
            .build()
            .toUriString();
    getRedirectStrategy().sendRedirect(request, response, target);
  }
}
