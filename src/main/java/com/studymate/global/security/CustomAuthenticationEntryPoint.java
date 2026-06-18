package com.studymate.global.security;

import com.studymate.global.exception.ErrorCode;
import com.studymate.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** 미인증/토큰 오류 시 401 + ErrorResponse(JSON) 반환. */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    Object attr = request.getAttribute(JwtAuthenticationFilter.ERROR_CODE_ATTRIBUTE);
    ErrorCode code = (attr instanceof ErrorCode ec) ? ec : ErrorCode.UNAUTHORIZED;
    ErrorResponse body = ErrorResponse.of(code, request.getRequestURI());

    response.setStatus(code.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(toJson(body));
  }

  private String toJson(ErrorResponse r) {
    return String.format(
        "{\"code\":\"%s\",\"message\":\"%s\",\"status\":%d,\"path\":\"%s\",\"timestamp\":\"%s\"}",
        r.code(), r.message(), r.status(), r.path(), r.timestamp());
  }
}
