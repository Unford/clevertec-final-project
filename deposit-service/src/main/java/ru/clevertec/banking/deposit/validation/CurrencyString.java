package ru.clevertec.banking.deposit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.clevertec.banking.deposit.validation.validator.CurrencyStringValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyStringValidator.class)
public @interface CurrencyString {
    String message() default "Invalid currency";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
