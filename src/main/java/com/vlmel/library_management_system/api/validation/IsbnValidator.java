package com.vlmel.library_management_system.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isBlank()) {
            return true;
        }

        String normalized = isbn.replaceAll("[- ]", "");

        if (normalized.length() == 13) {
            return normalized.matches("97[89]\\d{10}");
        }

        if (normalized.length() == 10) {
            return normalized.matches("\\d{9}[\\dXx]");
        }

        return false;
    }
}
