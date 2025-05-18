package study.goorm.domain.model.exception.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.goorm.domain.model.exception.annotation.CheckPageSize;
import study.goorm.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class CheckPageSizeValidator implements ConstraintValidator<CheckPageSize, Integer> {

    @Override
    public void initialize(CheckPageSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer pageSize, ConstraintValidatorContext context) {
        boolean isValid = pageSize >= 1;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.PAGE_SIZE_UNDER_ONE.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
