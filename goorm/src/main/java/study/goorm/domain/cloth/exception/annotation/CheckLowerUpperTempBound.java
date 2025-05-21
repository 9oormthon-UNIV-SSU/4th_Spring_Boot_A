package study.goorm.domain.cloth.exception.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import study.goorm.domain.cloth.exception.validator.CheckLowerUpperTempBoundValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckLowerUpperTempBoundValidator.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckLowerUpperTempBound {

    String message() default "상한 온도는 하한 온도보다 높아야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
