package com.vlmel.library_management_system.api.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void createBookRequest_valid() {
    var request = validCreateBookRequest();
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  void createBookRequest_rejectsBlankTitle() {
    var request = validCreateBookRequest();
    request.setTitle("  ");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void createBookRequest_rejectsBlankAuthor() {
    var request = validCreateBookRequest();
    request.setAuthor("");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void createBookRequest_rejectsNullPublishedYear() {
    var request = validCreateBookRequest();
    request.setPublishedYear(null);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void createBookRequest_rejectsInvalidIsbn() {
    var request = validCreateBookRequest();
    request.setIsbn("123");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void createBookRequest_rejectsFuturePublishedYear() {
    var request = validCreateBookRequest();
    request.setPublishedYear(Year.now().getValue() + 1);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void updateBookRequest_allowsEmptyFields() {
    assertTrue(validator.validate(new UpdateBookRequest()).isEmpty());
  }

  @Test
  void updateBookRequest_validatesProvidedIsbn() {
    var request = new UpdateBookRequest();
    request.setIsbn("invalid");
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void updateBookRequest_validatesProvidedPublishedYear() {
    var request = new UpdateBookRequest();
    request.setPublishedYear(Year.now().getValue() + 1);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  void updateCopyBookStatusRequest_requiresAvailable() {
    assertFalse(validator.validate(new UpdateCopyBookStatusRequest()).isEmpty());
  }

  @Test
  void updateCopyBookStatusRequest_valid() {
    var request = new UpdateCopyBookStatusRequest();
    request.setAvailable(true);
    assertTrue(validator.validate(request).isEmpty());
  }

  private static CreateBookRequest validCreateBookRequest() {
    var request = new CreateBookRequest();
    request.setTitle("Clean Code");
    request.setAuthor("Robert Martin");
    request.setIsbn("9780306406157");
    request.setPublishedYear(2008);
    return request;
  }
}
