package ru.clevertec.banking.deposit.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.service.DepositService;
import ru.clevertec.banking.deposit.validation.UniqueAccountIban;

@Component
@RequiredArgsConstructor
public class UniqueAccountIbanValidator implements ConstraintValidator<UniqueAccountIban, CreateDepositRequest> {
    private final DepositService depositService;

    @Override
    public boolean isValid(CreateDepositRequest value,
                           ConstraintValidatorContext context) {
        String iban = value.getAccInfo().getAccIban();
        if (depositService.isDepositExistByIban(iban)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Deposit with this account iban already exists")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }


}
