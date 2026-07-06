package com.vlmel.library_management_system.api.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishedYearValidationTest {

    private PublishedYearValidation validator;

    @BeforeEach
    void setUp() {
        validator = new PublishedYearValidation();
        validator.initialize(null);
    }

    @Test
    void acceptsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void acceptsValidYears() {
        assertTrue(validator.isValid(-3000, null));
        assertTrue(validator.isValid(1999, null));
        assertTrue(validator.isValid(Year.now().getValue(), null));
    }

    @Test
    void rejectsFutureYear() {
        assertFalse(validator.isValid(Year.now().getValue() + 1, null));
    }

    @Test
    void rejectsYearBelowMinimum() {
        assertFalse(validator.isValid(-3001, null));
    }
}
