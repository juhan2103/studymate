package com.studymate.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 전역 에러 코드. 도메인 기능 추가 시 여기에 확장한다 (기획서 7.2). */
@Getter
public enum ErrorCode {
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
