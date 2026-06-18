package com.studymate.global.exception;

import java.time.LocalDateTime;

/** 통일된 에러 응답 바디 (기획서 7.2). */
public record ErrorResponse(
    String code, String message, int status, String path, LocalDateTime timestamp) {

  public static ErrorResponse of(ErrorCode errorCode, String path) {
    return of(errorCode, errorCode.getMessage(), path);
  }

  public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
    return new ErrorResponse(
        errorCode.name(), message, errorCode.getStatus().value(), path, LocalDateTime.now());
  }
}
