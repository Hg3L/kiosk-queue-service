package com.thh.kiosk.queue.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;

public class Ipv4AddressValidator implements ConstraintValidator<Ipv4Address, String> {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return IPV4_PATTERN.matcher(value).matches();
    }
}
