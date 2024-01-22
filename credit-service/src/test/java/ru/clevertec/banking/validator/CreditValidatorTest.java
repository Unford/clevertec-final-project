package ru.clevertec.banking.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.repository.CreditRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static ru.clevertec.banking.util.CreditFactory.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CreditValidatorTest {
    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private CreditRepository repository;
    @InjectMocks
    private CreditValidator validator;

    @Test
    @DisplayName("test should return true, when credit with this iban or contract number not exist")
    void isValidTest() {
        CreditRequest request = getRequest();

        doReturn(false)
                .when(repository).existsCreditByContractNumber(Mockito.anyString());
        doReturn(false)
                .when(repository).existsCreditByIban(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("test should return false, when credit with this iban or contract number is exist")
    void isNotValidTest() {
        CreditRequest request = getRequest();

        doReturn(true)
                .when(repository).existsCreditByContractNumber(Mockito.anyString());
        doReturn(true)
                .when(repository).existsCreditByIban(Mockito.anyString());
        doReturn(constraintViolationBuilder)
                .when(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("test should return false, when credit with this iban or contract number is exist")
    void isNotValidSecondTest() {
        CreditRequest request = getRequest();

        doReturn(false)
                .when(repository).existsCreditByContractNumber(Mockito.anyString());
        doReturn(true)
                .when(repository).existsCreditByIban(Mockito.anyString());
        doReturn(constraintViolationBuilder)
                .when(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isFalse();
    }
}
