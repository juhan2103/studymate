package com.studymate.global.exception;

import lombok.Getter;

/** 비즈니스 규칙 위반 예외. ErrorCode로 HTTP 상태·메시지를 전달한다. */
@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
