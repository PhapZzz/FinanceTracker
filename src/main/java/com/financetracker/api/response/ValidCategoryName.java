package com.financetracker.api.response;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCategoryNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoryName {

    String message() default "Category name is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
