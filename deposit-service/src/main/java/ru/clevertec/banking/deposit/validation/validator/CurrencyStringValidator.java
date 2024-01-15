package ru.clevertec.banking.deposit.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.clevertec.banking.deposit.validation.CurrencyString;

import java.util.Currency;

public class CurrencyStringValidator implements ConstraintValidator<CurrencyString, String> {
    @Override
    public void initialize(CurrencyString constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .anyMatch(c -> c.equals(value));
    }
}
