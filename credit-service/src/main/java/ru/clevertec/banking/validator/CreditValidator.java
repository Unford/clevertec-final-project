package ru.clevertec.banking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.repository.CreditRepository;

@Component
@RequiredArgsConstructor
public class CreditValidator implements ConstraintValidator<CreditValidation, CreditRequest> {
    private final CreditRepository repository;

    @Override
    public void initialize(CreditValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreditRequest value, ConstraintValidatorContext context) {
        boolean existCreditByContractNumber = repository.existsCreditByContractNumber(value.contractNumber());
        boolean existCreditByIban = repository.existsCreditByIban(value.iban());

        boolean result = true;

        if (existCreditByContractNumber) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An credit with such an contract number already exists")
                    .addConstraintViolation();
            result = false;
        } else if (existCreditByIban) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An credit with such an iban already exists")
                    .addConstraintViolation();
            result = false;
        }
        return result;
    }
}
