package ru.clevertec.banking.deposit.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.service.DepositService;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringUnitCompositeTest;

import java.time.LocalDate;

@SpringUnitCompositeTest
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CustomValidatorTest {


    @Mock
    ConstraintValidatorContext validatorContext;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;


    @Autowired
    RandomDepositFactory depositFactory;

    @Nested
    class OpenDateBeforeExpirationValidatorTest {
        @InjectMocks
        OpenDateBeforeExpirationValidator expirationValidator;




        @Test
        void shouldReturnTrueForOpenDateBeforeExpirationDate() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getDepInfo()
                    .setExpDate(depositRequest.getAccInfo().getAccOpenDate().plusMonths(12));

            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
        }

        @Test
        void shouldReturnTrueForOpenDateAndExpirationDateNull() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getDepInfo().setExpDate(null);
            depositRequest.getAccInfo().setAccOpenDate(null);

            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
        }

        @Test
        void shouldReturnTrueWhenOpenDateNotNullButExpirationDateIsNull() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getDepInfo().setExpDate(null);

            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
        }

        @Test
        void shouldReturnTrueWhenOpenDateIsNullButExpirationDateIsFuture() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getAccInfo().setAccOpenDate(null);

            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
        }

        @Test
        void shouldReturnFalseWhenOpenDateBeforeExpirationDate() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getAccInfo().setAccOpenDate(LocalDate.now());
            depositRequest.getDepInfo().setExpDate(LocalDate.now().minusMonths(1));

            Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                    .thenReturn(constraintViolationBuilder);


            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isFalse();
            Mockito.verify(validatorContext).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
            Mockito.verify(constraintViolationBuilder).addConstraintViolation();
        }

        @Test
        void shouldReturnFalseWhenOpenDateIsNullAndExpirationDateBeforeNow() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.getAccInfo().setAccOpenDate(null);
            depositRequest.getDepInfo().setExpDate(LocalDate.now().minusMonths(1));

            Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                    .thenReturn(constraintViolationBuilder);


            boolean actual = expirationValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isFalse();
            Mockito.verify(validatorContext).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
            Mockito.verify(constraintViolationBuilder).addConstraintViolation();
        }

    }

    @Nested
    class UniqueAccountIbanValidatorTest {

        @Spy
        DepositService depositService = new DepositService(null, null, null);
        @InjectMocks
        UniqueAccountIbanValidator uniqueAccountIbanValidator ;


        @Test
        void shouldReturnFalseWhenIbanNotUnique() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();

            Mockito.doReturn(true).when(depositService).isDepositExistByIban(Mockito.anyString());
            Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
                    .thenReturn(constraintViolationBuilder);


            boolean actual = uniqueAccountIbanValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isFalse();
            Mockito.verify(validatorContext).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
            Mockito.verify(constraintViolationBuilder).addConstraintViolation();
        }

        @Test
        void shouldReturnTrueWhenIbanNotFound() {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();

            Mockito.doReturn(false).when(depositService).isDepositExistByIban(Mockito.anyString());

            boolean actual = uniqueAccountIbanValidator.isValid(depositRequest, validatorContext);

            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
            Mockito.verify(constraintViolationBuilder, Mockito.never()).addConstraintViolation();
        }

    }

    @Nested
    class CurrencyStringValidatorTest {

        @InjectMocks
        CurrencyStringValidator currencyStringValidator;


        @ParameterizedTest
        @ValueSource(strings = {"BYN", "RUB", "TMT", "UAH", "USD", "EUR"})
        void shouldReturnTrueWhenCurrencyExist(String currency) {

            boolean actual = currencyStringValidator.isValid(currency, validatorContext);
            Assertions.assertThat(actual).isTrue();
            Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
            Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
            Mockito.verify(constraintViolationBuilder, Mockito.never()).addConstraintViolation();
        }

        @ParameterizedTest
        @ValueSource(strings = {"HELP", "BBB", "012", "", "WHO AM I", "YES"})
        void shouldReturnFalseWhenCurrencyNotExist(String currency) {
            boolean actual = currencyStringValidator.isValid(currency, validatorContext);
            Assertions.assertThat(actual).isFalse();
        }
    }
}
