package com.jiraksilgame.common.validation;

import com.jiraksilgame.common.CommonConstants;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/** 
 * 게임 코드 형식 검증 애노테이션
 *  <p>Bean Validation 조합 제약으로 @Pattern(CommonConstants.GAME_CODE_REGEX) 사용</p>
 */
@Documented
@Constraint(validatedBy = {}) // 조합 제약(컴포지트), 별도 Validator 불필요
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = CommonConstants.GAME_CODE_REGEX)
public @interface GameCode {
    /** 오류 메시지 */
    String message() default "게임 코드 형식이 올바르지 않습니다. (A~Z, 0~9 조합의 12자리)";
    /** 검증 그룹 */
    Class<?>[] groups() default {};
    /** 페이로드 */
    Class<? extends Payload>[] payload() default {};
}
