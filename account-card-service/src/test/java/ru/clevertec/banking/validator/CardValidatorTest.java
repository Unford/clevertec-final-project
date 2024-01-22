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
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.validator.CardValidator;
import ru.clevertec.banking.repository.CardRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static ru.clevertec.banking.util.CardFactory.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CardValidatorTest {

    @Mock
    private ConstraintValidatorContext validatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private CardValidator validator;

    @Test
    @DisplayName("test should return true, when card with this card_number not exist")
    void isValidTest() {
        CardRequest request = getCardRequest();

        doReturn(false)
                .when(cardRepository).existsByCardNumber(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("test should return false, when card with this card_number is exist")
    void isNotValidTest() {
        CardRequest request = getCardRequest();

        doReturn(true)
                .when(cardRepository).existsByCardNumber(Mockito.anyString());
        doReturn(constraintViolationBuilder)
                .when(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());

        boolean actual = validator.isValid(request, validatorContext);

        assertThat(actual).isFalse();
    }
}
