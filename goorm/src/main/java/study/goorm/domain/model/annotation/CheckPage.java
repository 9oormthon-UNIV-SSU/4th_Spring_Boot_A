package study.goorm.domain.model.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import study.goorm.domain.model.validator.CheckPageValidator;

import java.lang.annotation.*;

@Documented // 사용자 Custom 애노테이션 만들 때 사용
@Constraint(validatedBy = CheckPageValidator.class) // ValidatedBy → CheckPageValidator가 검증을 수행한다.
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER}) // 어디에 어노테이션을 적용시킬지 정합니다. 클래스? 필드? 등
@Retention(RetentionPolicy.RUNTIME) // 어노테이션의 생명 주기
public @interface CheckPage {
    String message() default "페이지는 1이상 부터 입력이 가능합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
