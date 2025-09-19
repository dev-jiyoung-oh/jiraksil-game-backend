package com.jiraksilgame.common.validation;

import com.jiraksilgame.common.CommonConstants;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/** 게임 코드 - 커스텀 애노테이션 */
@Documented
@Constraint(validatedBy = {}) // 조합 제약(컴포지트), 별도 Validator 불필요
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = CommonConstants.GAME_CODE_REGEX)
public @interface GameCode {
  String message() default "Invalid game code";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
