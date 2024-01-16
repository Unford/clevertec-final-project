package ru.clevertec.banking.deposit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.clevertec.banking.deposit.validation.validator.UniqueAccountIbanValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueAccountIbanValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAccountIban {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
