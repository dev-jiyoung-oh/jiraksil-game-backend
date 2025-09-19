package com.jiraksilgame.common.error;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.Getter;

/** 
 * 공통 런타임 예외
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorCode code;

    // ========== 생성자 ==========

    /**
     * 에러코드와 메시지로 예외 생성
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     */
    public AppException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 에러코드·메시지·원인으로 예외 생성
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param cause 원인 예외
     */
    public AppException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    // ========== 편의 팩토리 ==========

    /**
     * 400 Bad Request용 예외 생성
     *
     * @param msg 에러 메시지
     * @return AppException 인스턴스
     */
    public static AppException badRequest(String msg) { return new AppException(ErrorCode.BAD_REQUEST, msg); }

    /**
     * 404 Not Found용 예외 생성
     *
     * @param msg 에러 메시지
     * @return AppException 인스턴스
     */
    public static AppException notFound(String msg) { return new AppException(ErrorCode.NOT_FOUND, msg); }

    /**
     * 409 Conflict용 예외 생성
     *
     * @param msg 에러 메시지
     * @return AppException 인스턴스
     */
    public static AppException conflict(String msg) { return new AppException(ErrorCode.CONFLICT, msg); }

    /**
     * 500 Internal Error용 예외 생성
     *
     * @param msg 에러 메시지
     * @return AppException 인스턴스
     */
    public static AppException internal(String msg) { return new AppException(ErrorCode.INTERNAL_ERROR, msg); }


    // ========== 내부 타입/유틸 ==========

    /** 에러 코드 정의 */
    public enum ErrorCode {
        BAD_REQUEST, NOT_FOUND, CONFLICT, INTERNAL_ERROR
    }

    /** DB 제약 위반 판별 유틸 */
    public static final class Db {

        private Db() {}

        /**
         * DataIntegrityViolationException의 제약명으로 특정 제약 위반 여부 판별
         * <p>Hibernate ConstraintViolationException이 원인일 때만 검사</p>
         * 
         * @param e 데이터 무결성 예외
         * @param names 비교할 제약명 목록(대소문자 무시)
         * @return 일치하는 제약명이 있으면 true
         */
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
