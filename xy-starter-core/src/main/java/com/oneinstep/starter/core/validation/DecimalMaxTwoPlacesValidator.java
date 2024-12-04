package com.oneinstep.starter.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * 最多保留两位小数的校验器
 */
public class DecimalMaxTwoPlacesValidator implements ConstraintValidator<DecimalMaxTwoPlacesGroup, BigDecimal> {

    @Override
    public void initialize(DecimalMaxTwoPlacesGroup constraintAnnotation) {

    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            // null values are valid
            return true;
        }

        // Use a regular expression to check if the BigDecimal value has at most two decimal places
        String stringValue = value.stripTrailingZeros().toPlainString();
        return stringValue.matches("^\\d+(\\.\\d{1,2})?$");
    }
}