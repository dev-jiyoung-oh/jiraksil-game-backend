package com.jiraksilgame.common.web;

import com.jiraksilgame.common.error.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/** 전역 예외 처리 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** AppException → HTTP 응답 */
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ErrorBody> handleApp(AppException ex, HttpServletRequest req) {
    HttpStatus status = switch (ex.getCode()) {
      case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
      case NOT_FOUND  -> HttpStatus.NOT_FOUND;
      case CONFLICT   -> HttpStatus.CONFLICT;
      default         -> HttpStatus.INTERNAL_SERVER_ERROR;
    };

    logApp(status, ex, req);

    return ResponseEntity.status(status)
        .body(ErrorBody.of(ex.getCode().name(), ex.getMessage()));
  }

  /** @Valid 바인딩 실패(JSON body) */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleInvalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(this::formatFieldError)
        .collect(Collectors.joining("; "));
    logClient400("[Validation]", req.getRequestURI(), msg);
    return ErrorBody.of("BAD_REQUEST", msg.isBlank() ? "Validation failed" : msg);
  }

  /** @Validated on @RequestParam/@PathVariable 등 */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    String msg = ex.getConstraintViolations().stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .collect(Collectors.joining("; "));
    logClient400("[Constraint]", req.getRequestURI(), msg);
    return ErrorBody.of("BAD_REQUEST", msg.isBlank() ? "Constraint violation" : msg);
  }

  /** JSON 파싱 오류 */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorBody handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
    String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    logClient400("[Parse]", req.getRequestURI(), cause);
    return ErrorBody.of("BAD_REQUEST", "Malformed JSON or incompatible data type");
  }

  /** 기타 에러 */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorBody handleOthers(Exception ex, HttpServletRequest req) {
    log.error("[Unhandled] status=500 path={} msg={}", req.getRequestURI(), sanitize(ex.getMessage()), ex);
    return ErrorBody.of("INTERNAL_ERROR", "Unexpected server error");
  }

  // ========== 로깅 헬퍼(형식 고정) ==========

  private void logApp(HttpStatus status, AppException ex, HttpServletRequest req) {
    String path = req.getRequestURI();
    String code = ex.getCode().name();
    String msg  = sanitize(ex.getMessage());

    if (status.is5xxServerError()) {
      log.error("AppException status={} code={} path={} msg={}", status.value(), code, path, msg, ex);
    } else {
      log.warn("AppException status={} code={} path={} msg={}", status.value(), code, path, msg);
    }
  }

  private void logClient400(String kind, String path, String msg) {
    log.warn("{} status=400 path={} msg={}", kind, path, sanitize(msg));
  }

  private String sanitize(String s) {
    return (s == null) ? "" : s.replaceAll("[\\r\\n\\t]", " ");
  }

  private String formatFieldError(FieldError fe) {
    String field = fe.getField();
    String msg   = fe.getDefaultMessage();
    return field + ": " + (msg != null ? msg : "invalid");
  }

  // ========== 표준 에러 바디 ==========

  @Getter
  @AllArgsConstructor
  public static class ErrorBody {
    private String code;
    private String message;
    private Instant timestamp;

    public static ErrorBody of(String code, String message) {
      return new ErrorBody(code, message, Instant.now());
    }
  }
}
