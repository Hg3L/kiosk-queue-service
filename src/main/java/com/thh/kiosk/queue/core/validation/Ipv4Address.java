package com.thh.kiosk.queue.core.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Ipv4AddressValidator.class)
public @interface Ipv4Address {

    String message() default "{counter.error.ip_invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
