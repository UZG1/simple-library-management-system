package com.vlmel.library_management_system.api.controller;

import com.vlmel.library_management_system.exception.BookCopyNotFoundException;
import com.vlmel.library_management_system.exception.BookIsbnAlreadyExistsException;
import com.vlmel.library_management_system.exception.BookNotFoundException;
import com.vlmel.library_management_system.exception.BookTitleAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBookNotFound(BookNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BookCopyNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCopyNotFound(BookCopyNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BookTitleAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleBookTitleConflict(BookTitleAlreadyExistsException ex,
                                                                 HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BookIsbnAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleBookIsbnConflict(BookIsbnAlreadyExistsException ex,
                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                          HttpServletRequest request) {
        String parameterName = ex.getName();
        String providedValue = ex.getValue() == null ? "null" : ex.getValue().toString();
        String requiredType = ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName();
        String message = "Invalid value '" + providedValue + "' for parameter '" + parameterName
                + "'. Expected type: " + requiredType;
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                      HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request.getRequestURI(),
                List.of(ex.getClass().getSimpleName())
        );
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status,
                                                        String message,
                                                        String path,
                                                        List<String> details) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setProperty("path", path);
        if (details != null && !details.isEmpty()) {
            problemDetail.setProperty("details", details);
        }

        return ResponseEntity.status(status).body(problemDetail);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
