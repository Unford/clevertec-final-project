package ru.clevertec.banking.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class AccountValidator implements ConstraintValidator<AccountValidation, AccountRequest> {
    private final AccountRepository repository;

    @Override
    public void initialize(AccountValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AccountRequest value, ConstraintValidatorContext context) {
        boolean existAccountByIban = repository.existsAccountByIban(value.iban());

        if (existAccountByIban) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("An account with such an iban already exists")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
