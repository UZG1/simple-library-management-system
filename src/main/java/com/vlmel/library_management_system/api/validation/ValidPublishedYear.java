package com.vlmel.library_management_system.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PublishedYearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPublishedYear {

    String message() default "Published year must be between 1000 and the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
