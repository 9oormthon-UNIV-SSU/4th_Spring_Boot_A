package study.goorm.domain.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.goorm.domain.model.annotation.CheckPage;
import study.goorm.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class CheckPageValidator implements ConstraintValidator<CheckPage, Integer> {
//ConstraintValidator 인터페이스의 구현체이며 CheckPage 어노테이션에 대한 로직을 담을 것이며 Integer를 검증할 것을 명시합니다.
    @Override
    public void initialize(CheckPage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    //ConstraintValidator의 매서드 isValid를 구현하며 내부에 구현에 대한 세부 사항이 있으며, 유효하지 않을 경우 ConstraintViolation을 만들게 됩니다.
    public boolean isValid(Integer page, ConstraintValidatorContext context) {
        boolean isValid = page >= 1;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.PAGE_UNDER_ONE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}

