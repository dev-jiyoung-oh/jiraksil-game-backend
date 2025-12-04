package com.jiraksilgame.common.web;

import com.jiraksilgame.common.error.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
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

/** 
 * 전역 예외 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AppException을 HTTP 상태와 표준 에러 바디로 매핑
     * <p>에러코드에 따라 상태코드를 결정하고 로깅 후 응답을 생성</p>
     *
     * @param ex 처리할 AppException
     * @param req HTTP 요청
     * @return 상태코드와 에러 바디를 담은 ResponseEntity
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorBody> handleApp(AppException ex, HttpServletRequest req) {
        HttpStatus status = switch (ex.getCode()) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        logApp(status, ex, req);

        return ResponseEntity.status(status)
                .body(ErrorBody.of(ex.getCode().name(), ex.getMessage()));
    }

    /**
     * @Valid 바인딩 실패 400 응답 생성
     * <p>필드 오류 메시지를 결합해 표준 에러 바디로 반환</p>
     *
     * @param ex 바인딩 예외
     * @param req HTTP 요청
     * @return BAD_REQUEST 에러 바디
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleInvalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        logClient400("[Validation]", req.getRequestURI(), msg);
        return ErrorBody.of("BAD_REQUEST", msg.isBlank() ? "Validation failed" : msg);
    }

    /**
     * @Validated 제약 위반 400 응답 생성
     * <p>파라미터/경로 변수의 제약 위반 메시지를 결합해 반환</p>
     *
     * @param ex 제약 위반 예외
     * @param req HTTP 요청
     * @return BAD_REQUEST 에러 바디
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
                
        // 첫 번째 violation만 뽑음
        String msg = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("잘못된 요청입니다.");

        logClient400("[Constraint]", req.getRequestURI(), msg);

        return ErrorBody.of("BAD_REQUEST", msg);
    }

    /**
     * JSON 파싱 오류 400 응답 생성
     * <p>본문 파싱 실패 또는 타입 불일치를 표준 에러로 반환</p>
     *
     * @param ex 메시지 읽기 예외
     * @param req HTTP 요청
     * @return BAD_REQUEST 에러 바디
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        logClient400("[Parse]", req.getRequestURI(), cause);
        return ErrorBody.of("BAD_REQUEST", "Malformed JSON or incompatible data type");
    }

    /**
     * 그 외 예외 500 응답 생성
     * <p>스택트레이스와 함께 로깅 후 내부 오류 바디 반환</p>
     *
     * @param ex 처리되지 않은 예외
     * @param req HTTP 요청
     * @return INTERNAL_ERROR 에러 바디
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorBody handleOthers(Exception ex, HttpServletRequest req) {
        log.error("[Unhandled] status=500 path={} msg={}", req.getRequestURI(), sanitize(ex.getMessage()), ex);
        return ErrorBody.of("INTERNAL_ERROR", "Unexpected server error");
    }

    // ========== 로깅 헬퍼(형식 고정) ==========

    /**
     * AppException 로깅
     * <p>5xx는 error, 4xx는 warn 레벨로 기록</p>
     *
     * @param status 매핑된 HTTP 상태
     * @param ex AppException
     * @param req HTTP 요청
     * @return 없음
     */
    private void logApp(HttpStatus status, AppException ex, HttpServletRequest req) {
        String path = req.getRequestURI();
        String code = ex.getCode().name();
        String msg = sanitize(ex.getMessage());

        if (status.is5xxServerError()) {
            log.error("AppException status={} code={} path={} msg={}", status.value(), code, path, msg, ex);
        } else {
            log.warn("AppException status={} code={} path={} msg={}", status.value(), code, path, msg);
        }
    }

    /**
     * 클라이언트 입력 오류 로깅
     * <p>요청 경로와 메시지를 warn 레벨로 기록</p>
     *
     * @param kind 오류 종류 태그
     * @param path 요청 경로
     * @param msg 오류 메시지
     * @return 없음
     */
    private void logClient400(String kind, String path, String msg) {
        log.warn("{} status=400 path={} msg={}", kind, path, sanitize(msg));
    }

    /**
     * 로그 안전화를 위한 메시지 정제
     * <p>개행과 탭 문자를 공백으로 치환</p>
     *
     * @param s 원본 문자열
     * @return 정제된 문자열
     */
    private String sanitize(String s) {
        return (s == null) ? "" : s.replaceAll("[\\r\\n\\t]", " ");
    }

    /**
     * 필드 오류를 'field: message' 형식 문자열로 변환
     *
     * @param fe FieldError 객체
     * @return 변환된 메시지
     */
    private String formatFieldError(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        return field + ": " + (msg != null ? msg : "invalid");
    }

    // ========== 표준 에러 바디 ==========

    /** 표준 에러 응답 바디 */
    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private String code;
        private String message;
        private Instant timestamp;

        /**
         * 현재 시각을 포함한 에러 바디 생성
         *
         * @param code 에러 코드
         * @param message 에러 메시지
         * @return ErrorBody 인스턴스
         */
        public static ErrorBody of(String code, String message) {
            return new ErrorBody(code, message, Instant.now());
        }
    }
}
