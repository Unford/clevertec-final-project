package ru.clevertec.banking.deposit.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.validation.OpenDateBeforeExpiration;

import java.time.LocalDate;
import java.util.Objects;

public class OpenDateBeforeExpirationValidator implements ConstraintValidator<OpenDateBeforeExpiration, CreateDepositRequest> {
    @Override
    public boolean isValid(CreateDepositRequest value, ConstraintValidatorContext context) {
        LocalDate openDate = value.getAccInfo().getAccOpenDate();
        LocalDate expDate = value.getDepInfo().getExpDate();
        LocalDate now = LocalDate.now();
        boolean isValid = (Objects.isNull(expDate))
                || (Objects.isNull(openDate) && expDate.isAfter(now))
                || (Objects.nonNull(openDate) && expDate.isAfter(openDate));
        if (!isValid){
            addValidationMessage(context, "Expiration date should be after open date");
        }
        return isValid;
    }

    private void addValidationMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
