package com.jiraksilgame.common.error;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.Getter;

/** 공통 예외 */
@Getter
public class AppException extends RuntimeException {
  private final ErrorCode code;

  public AppException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }
  public AppException(ErrorCode code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  // 편의 팩토리
  public static AppException badRequest(String msg) { return new AppException(ErrorCode.BAD_REQUEST, msg); }
  public static AppException notFound(String msg)   { return new AppException(ErrorCode.NOT_FOUND, msg); }
  public static AppException conflict(String msg)   { return new AppException(ErrorCode.CONFLICT, msg); }
  public static AppException internal(String msg)   { return new AppException(ErrorCode.INTERNAL_ERROR, msg); }

  public enum ErrorCode {
    BAD_REQUEST, NOT_FOUND, CONFLICT, INTERNAL_ERROR
  }

  /** DB 제약 위반 탐지 유틸(제약명 기반) */
  public static final class Db {
    private Db() {}
    public static boolean isConstraint(DataIntegrityViolationException e, String... names) {
      Throwable cause = e.getCause();

      if (cause instanceof ConstraintViolationException cve) {
        String name = cve.getConstraintName();
        
        if (name == null) return false;
        
        for (String n : names) {
          if (name.equalsIgnoreCase(n)) return true;
        }
      }
      
      return false;
    }
  }
}
