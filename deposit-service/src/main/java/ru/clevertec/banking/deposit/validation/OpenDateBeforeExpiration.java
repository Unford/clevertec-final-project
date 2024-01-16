package ru.clevertec.banking.deposit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.clevertec.banking.deposit.validation.validator.OpenDateBeforeExpirationValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OpenDateBeforeExpirationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenDateBeforeExpiration {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
