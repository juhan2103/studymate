package com.studymate.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 전역 예외 핸들러. 모든 에러를 ErrorResponse 형식으로 통일한다. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(
      BusinessException e, HttpServletRequest request) {
    ErrorCode code = e.getErrorCode();
    return ResponseEntity.status(code.getStatus())
        .body(ErrorResponse.of(code, e.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .orElse(ErrorCode.VALIDATION_FAILED.getMessage());
    return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
        .body(ErrorResponse.of(ErrorCode.VALIDATION_FAILED, message, request.getRequestURI()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
    return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, request.getRequestURI()));
  }
}
