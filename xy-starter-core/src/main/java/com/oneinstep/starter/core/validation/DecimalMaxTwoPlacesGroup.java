package com.oneinstep.starter.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DecimalMaxTwoPlacesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMaxTwoPlacesGroup {
    String message() default "订单金额小数位数不能超过两位";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
