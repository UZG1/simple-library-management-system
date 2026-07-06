package com.vlmel.library_management_system.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class PublishedYearValidator implements ConstraintValidator<ValidPublishedYear, Integer> {

    private static final int MIN_YEAR = 1000;

    @Override
    public boolean isValid(Integer publishedYear, ConstraintValidatorContext context) {
        if (publishedYear == null) {
            return true;
        }

        int currentYear = Year.now().getValue();
        return publishedYear >= MIN_YEAR && publishedYear <= currentYear;
    }
}
