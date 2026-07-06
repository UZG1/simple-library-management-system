package com.vlmel.library_management_system.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidation implements ConstraintValidator<Isbn, String> {

    private static final String ISBN_10_REGEX = "^\\d{9}[\\dX]$";
    private static final String ISBN_13_REGEX = "^97[89]\\d{10}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String isbn = value.replace("-", "")
                .replace(" ", "")
                .toUpperCase();

        if (isbn.matches(ISBN_10_REGEX)) {
            return isValidIsbn10(isbn);
        }

        if (isbn.matches(ISBN_13_REGEX)) {
            return isValidIsbn13(isbn);
        }

        return false;
    }

    private boolean isValidIsbn10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }

        int expected = (11 - (sum % 11)) % 11;
        char checkChar = isbn.charAt(9);
        int actual = checkChar == 'X' ? 10 : checkChar - '0';

        return expected == actual;
    }

    private boolean isValidIsbn13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int expected = (10 - (sum % 10)) % 10;
        int actual = isbn.charAt(12) - '0';

        return expected == actual;
    }
}
