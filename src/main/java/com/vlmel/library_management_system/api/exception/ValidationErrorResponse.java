package com.vlmel.library_management_system.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private Map<String, String> errors;
}
