package com.vlmel.library_management_system.api.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsbnValidatorTest {

    private IsbnValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IsbnValidator();
        validator.initialize(null);
    }

    @Test
    void acceptsNullAndBlank() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("   ", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0306406152",
            "0-306-40615-2",
            "156884459X",
            "9780306406157",
            "978-0-306-40615-7"
    })
    void acceptsValidIsbn(String isbn) {
        assertTrue(validator.isValid(isbn, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0306406153",
            "9781234567890",
            "123",
            "abcdefghij"
    })
    void rejectsInvalidIsbn(String isbn) {
        assertFalse(validator.isValid(isbn, null));
    }

    @Test
    void acceptsLowercaseCheckDigitX() {
        assertTrue(validator.isValid("156884459x", null));
    }
}
