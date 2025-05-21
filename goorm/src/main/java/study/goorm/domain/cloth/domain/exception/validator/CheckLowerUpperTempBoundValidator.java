package study.goorm.domain.cloth.domain.exception.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.goorm.domain.cloth.domain.dto.ClothRequestDTO;
import study.goorm.domain.cloth.domain.exception.annotation.CheckLowerUpperTempBound;
import study.goorm.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class CheckLowerUpperTempBoundValidator implements ConstraintValidator<CheckLowerUpperTempBound, ClothRequestDTO.ClothCreateRequest> {

    @Override
    public void initialize(CheckLowerUpperTempBound constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ClothRequestDTO.ClothCreateRequest request, ConstraintValidatorContext context) {
        boolean isValid = request.getTempLowerBound() <= request.getTempUpperBound();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.LOWER_TEMP_BIGGER_THAN_UPPER_TEMP.toString()).addConstraintViolation();
        }

        return isValid;

    }
}