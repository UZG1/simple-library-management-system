package com.vlmel.library_management_system.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IsbnValidator.class)
public @interface Isbn {
    String message() default "Invalid ISBN number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
