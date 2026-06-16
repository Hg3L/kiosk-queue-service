package com.thh.kiosk.queue.core.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = AbsolutePathValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AbsolutePath {

    String message() default "{reset_time.error.export_path_invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
