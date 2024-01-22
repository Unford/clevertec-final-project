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
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.validator.AccountValidator;
import ru.clevertec.banking.repository.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static ru.clevertec.banking.util.AccountFactory.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class AccountValidatorTest {

    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountValidator validator;

    @Test
    @DisplayName("test should return true, when account with this iban not exist")
    void isValidTest() {
        AccountRequest request = getAccountRequest();

        doReturn(false)
                .when(accountRepository).existsAccountByIban(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("test should return false, when account with this iban is exist")
    void isNotValidTest() {
        AccountRequest request = getAccountRequest();

        doReturn(true)
                .when(accountRepository).existsAccountByIban(Mockito.anyString());
        doReturn(constraintViolationBuilder)
                .when(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isFalse();
    }
}
