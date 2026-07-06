package com.vlmel.library_management_system.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class PublishedYearValidation implements ConstraintValidator<PublishedYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value >= -3000 && value <= Year.now().getValue();
    }
}
