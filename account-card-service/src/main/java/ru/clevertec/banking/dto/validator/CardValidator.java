package ru.clevertec.banking.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.repository.CardRepository;

@Component
@RequiredArgsConstructor
public class CardValidator implements ConstraintValidator<CardValidation, CardRequest> {
    private final CardRepository repository;

    @Override
    public void initialize(CardValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CardRequest value, ConstraintValidatorContext context) {
        boolean existCardByCardNumber = repository.existsByCardNumber(value.card_number());

        if (existCardByCardNumber) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An card with such an card_number already exists")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
