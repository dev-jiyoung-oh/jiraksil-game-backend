package com.jiraksilgame.common.error;

import org.springframework.core.NestedExceptionUtils;

/**
 * 공통 예외 처리 유틸
 */
public final class ErrorUtil {

  private ErrorUtil() {}

  /**
   * 루트 원인(cause) 반환
   *
   * @param t 대상 예외(null 허용)
   * @return 루트 cause(널-세이프)
   */
  public static Throwable rootCause(Throwable t) {
    if (t == null) return null;
    Throwable most = NestedExceptionUtils.getMostSpecificCause(t);
    return (most != null) ? most : t;
  }

  /**
   * 루트 원인(cause)의 메시지 반환
   *
   * @param t 최상위 예외(null 허용)
   * @return 루트 메시지 문자열(널-세이프)
   */
  public static String rootMessage(Throwable t) {
    if (t == null) return "";
    Throwable r = rootCause(t);
    if (r == null) return "";
    String msg = r.getMessage();
    return (msg != null) ? msg : r.toString();
  }
}
