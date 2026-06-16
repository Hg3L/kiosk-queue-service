package com.thh.kiosk.queue.core.validation;


import com.thh.kiosk.queue.core.util.MessageUtils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbsolutePathValidator implements ConstraintValidator<AbsolutePath, String> {

    @Override
    public boolean isValid(String pathField, ConstraintValidatorContext context) {

        if (pathField == null || pathField.isBlank()) {
            return true;
        }

        try {
            Path path = Path.of(pathField);

            Path normalizedPath = path.normalize();
            if (!normalizedPath.isAbsolute()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("file.error.absolute_path_required")
                        )
                        .addConstraintViolation();
                return false;
            }

            if (pathField.contains("..")) {
                log.warn("Part traversal is detected");
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("file.error.path_invalid")
                        )
                        .addConstraintViolation();
                return false;
            }

            return true;

        } catch (InvalidPathException ex) {
            log.error("Invalid path: {}", pathField, ex);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageUtils.getMessage("file.error.path_invalid")
                    )
                    .addConstraintViolation();
            return false;
        }
    }
}
