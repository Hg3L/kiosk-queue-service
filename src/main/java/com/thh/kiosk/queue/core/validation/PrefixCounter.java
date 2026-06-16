package com.thh.kiosk.queue.core.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrefixCounterValidator.class)
public @interface PrefixCounter {

    String message() default "{counter.error.prefix_invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
