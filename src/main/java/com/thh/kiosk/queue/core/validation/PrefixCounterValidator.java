package com.thh.kiosk.queue.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;

public class PrefixCounterValidator implements ConstraintValidator<PrefixCounter, String> {

    private static final Pattern PREFIX_COUNTER_PATTERN = Pattern.compile("^[A-Z]$");

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return PREFIX_COUNTER_PATTERN.matcher(value).matches();
    }
}
